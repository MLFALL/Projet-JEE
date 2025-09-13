package sn.isi.immobilier.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import sn.isi.immobilier.dao.impl.*;
import sn.isi.immobilier.model.Enums.LeaseStatus;
import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"admin",
        "/app/admin/dashboard",
        "/app/admin/users",
        "/app/admin/buildings",
        "/app/admin/units",
        "/app/admin/leases",
        "/app/admin/payments",
        "/app/admin/applications"
})
public class AdminServlet extends HttpServlet {

    private UserDaoImpl userDao;
    private BuildingDaoImpl buildingDao;
    private UnitDaoImpl unitDao;
    private LeaseDaoImpl leaseDao;
    private PaymentDaoImpl paymentDao;
    private RentalApplicationDaoImpl applicationDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDaoImpl();
        buildingDao = new BuildingDaoImpl();
        unitDao = new UnitDaoImpl();
        leaseDao = new LeaseDaoImpl();
        paymentDao = new PaymentDaoImpl();
        applicationDao = new RentalApplicationDaoImpl();
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }
    private boolean isJsonRequest(HttpServletRequest req) {
        String acceptHeader = req.getHeader("Accept");
        return acceptHeader != null && acceptHeader.contains("application/json");
    }


    // --- GET ---
    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        String path = req.getServletPath();

        switch (path) {
            case "/app/admin/dashboard":
                req.setAttribute("totalUsers", userDao.findAll().size());
                req.setAttribute("totalBuildings", buildingDao.findAll().size());
                req.setAttribute("totalUnits", unitDao.findAll().size());
                req.setAttribute("totalLeases", leaseDao.findAll().size());
                req.setAttribute("totalPayments", paymentDao.findAll().size());
                req.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp").forward(req, resp);
                break;

            case "/app/admin/users":
                req.setAttribute("users", userDao.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/users/list.jsp").forward(req, resp);
                break;

            case "/app/admin/buildings":
                req.setAttribute("buildings", buildingDao.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/buildings/list.jsp").forward(req, resp);
                break;

            case "/app/admin/units":
                req.setAttribute("units", unitDao.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/units/list.jsp").forward(req, resp);
                break;

            case "/app/admin/leases":
                req.setAttribute("leases", leaseDao.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/leases/list.jsp").forward(req, resp);
                break;

            case "/app/admin/payments":
                req.setAttribute("payments", paymentDao.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/payments/list.jsp").forward(req, resp);
                break;

            case "/app/admin/applications":
                req.setAttribute("applications", applicationDao.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/applications/list.jsp").forward(req, resp);
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }*/

    // --- POST ---
   /* @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        String path = req.getServletPath();
        switch (path) {

            // ----------------- Users CRUD -----------------
            case "/app/admin/users":
                String userAction = req.getParameter("action");
                String userIdStr = req.getParameter("userId");

                if (userAction != null && userIdStr != null) {
                    Long userId = Long.parseLong(userIdStr);
                    switch (userAction) {
                        case "delete":
                            userDao.deleteById(userId);
                            break;
                        case "update":
                            String fullName = req.getParameter("fullName");
                            String roleStr = req.getParameter("role");
                            User userToUpdate = userDao.findById(userId).orElse(null);
                            if (userToUpdate != null) {
                                userToUpdate.setFullName(fullName);
                                userToUpdate.setRole(UserRole.valueOf(roleStr));
                                userDao.save(userToUpdate);
                            }
                            break;
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/app/admin/users");
                break;

            // ----------------- Buildings CRUD -----------------
            case "/app/admin/buildings":
                String buildingAction = req.getParameter("action");
                String buildingIdStr = req.getParameter("buildingId");
                if (buildingAction != null) {
                    switch (buildingAction) {
                        case "delete":
                            buildingDao.deleteById(Long.parseLong(buildingIdStr));
                            break;
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/app/admin/buildings");
                break;

            // ----------------- Units CRUD -----------------
            case "/app/admin/units":
                String unitAction = req.getParameter("action");
                String unitIdStr = req.getParameter("unitId");
                if (unitAction != null) {
                    switch (unitAction) {
                        case "delete":
                            unitDao.deleteById(Long.parseLong(unitIdStr));
                            break;
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/app/admin/units");
                break;

            // ----------------- Leases -----------------
            case "/app/admin/leases":
                String leaseAction = req.getParameter("action");
                String leaseIdStr = req.getParameter("leaseId");
                if ("terminate".equals(leaseAction) && leaseIdStr != null) {
                    Lease lease = leaseDao.findById(Long.parseLong(leaseIdStr)).orElse(null);
                    if (lease != null) {
                        lease.setStatus(LeaseStatus.valueOf("TERMINATED"));
                        leaseDao.save(lease);
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/app/admin/leases");
                break;

            // ----------------- Payments -----------------
            case "/app/admin/payments":
                // Ajouter si nécessaire CRUD ou validations
                resp.sendRedirect(req.getContextPath() + "/app/admin/payments");
                break;

            // ----------------- Applications -----------------
            case "/app/admin/applications":
                // Ajouter si nécessaire CRUD ou validations
                resp.sendRedirect(req.getContextPath() + "/app/admin/applications");
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }*/

    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    // --- GET ---
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJson(resp, "{\"error\":\"Accès refusé\"}");
            return;
        }

        String path = req.getServletPath();
        try {
            switch (path) {
                case "/app/admin/dashboard": {
                    long totalUsers = userDao.findAll().size();
                    long totalBuildings = buildingDao.findAll().size();
                    long totalUnits = unitDao.findAll().size();
                    long totalLeases = leaseDao.findAll().size();
                    long totalPayments = paymentDao.findAll().size();
                    String json = "{"
                            + "\"totalUsers\":" + totalUsers + ","
                            + "\"totalBuildings\":" + totalBuildings + ","
                            + "\"totalUnits\":" + totalUnits + ","
                            + "\"totalLeases\":" + totalLeases + ","
                            + "\"totalPayments\":" + totalPayments
                            + "}";
                    writeJson(resp, json);
                    break;
                }
                case "/app/admin/users": {
                    List<User> users = userDao.findAll();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < users.size(); i++) {
                        User u = users.get(i);
                        sb.append("{")
                                .append("\"id\":").append(u.getId()).append(",")
                                .append("\"email\":\"").append(u.getEmail()).append("\",")
                                .append("\"fullName\":\"").append(u.getFullName()).append("\",")
                                .append("\"role\":\"").append(u.getRole()).append("\",")
                                .append("\"active\":").append(u.isActive())
                                .append("}");
                        if (i < users.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    writeJson(resp, sb.toString());
                    break;
                }
                case "/app/admin/buildings": {
                    List<Building> list = buildingDao.findAll();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < list.size(); i++) {
                        Building b = list.get(i);
                        sb.append("{")
                                .append("\"id\":").append(b.getId()).append(",")
                                .append("\"name\":\"").append(escape(b.getName())).append("\",")
                                .append("\"city\":\"").append(escape(b.getCity())).append("\",")
                                .append("\"ownerId\":").append(b.getOwner() != null ? b.getOwner().getId() : "null")
                                .append("}");
                        if (i < list.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    writeJson(resp, sb.toString());
                    break;
                }
                case "/app/admin/units": {
                    List<sn.isi.immobilier.model.Unit> list = unitDao.findAll();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < list.size(); i++) {
                        sn.isi.immobilier.model.Unit u = list.get(i);
                        sb.append("{")
                                .append("\"id\":").append(u.getId()).append(",")
                                .append("\"unitNumber\":\"").append(escape(u.getUnitNumber())).append("\",")
                                .append("\"rooms\":").append(u.getRooms()).append(",")
                                .append("\"rentAmount\":").append(u.getRentAmount() != null ? u.getRentAmount() : "null").append(",")
                                .append("\"status\":\"").append(u.getStatus()).append("\",")
                                .append("\"buildingId\":").append(u.getBuilding() != null ? u.getBuilding().getId() : "null")
                                .append("}");
                        if (i < list.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    writeJson(resp, sb.toString());
                    break;
                }
                case "/app/admin/leases": {
                    List<Lease> list = leaseDao.findAll();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < list.size(); i++) {
                        Lease l = list.get(i);
                        sb.append("{")
                                .append("\"id\":").append(l.getId()).append(",")
                                .append("\"unitId\":").append(l.getUnit() != null ? l.getUnit().getId() : "null").append(",")
                                .append("\"tenantId\":").append(l.getTenant() != null ? l.getTenant().getId() : "null").append(",")
                                .append("\"ownerId\":").append(l.getOwner() != null ? l.getOwner().getId() : "null").append(",")
                                .append("\"startDate\":\"").append(l.getStartDate() != null ? l.getStartDate().toString() : "").append("\",")
                                .append("\"endDate\":\"").append(l.getEndDate() != null ? l.getEndDate().toString() : "").append("\",")
                                .append("\"rentAmount\":").append(l.getRentAmount() != null ? l.getRentAmount() : "null").append(",")
                                .append("\"status\":\"").append(l.getStatus()).append("\"")
                                .append("}");
                        if (i < list.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    writeJson(resp, sb.toString());
                    break;
                }
                case "/app/admin/payments": {
                    List<sn.isi.immobilier.model.Payment> list = paymentDao.findAll();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < list.size(); i++) {
                        sn.isi.immobilier.model.Payment p = list.get(i);
                        sb.append("{")
                                .append("\"id\":").append(p.getId()).append(",")
                                .append("\"leaseId\":").append(p.getLease() != null ? p.getLease().getId() : "null").append(",")
                                .append("\"dueDate\":\"").append(p.getDueDate() != null ? p.getDueDate().toString() : "").append("\",")
                                .append("\"amountDue\":").append(p.getAmountDue() != null ? p.getAmountDue() : "null").append(",")
                                .append("\"amountPaid\":").append(p.getAmountPaid() != null ? p.getAmountPaid() : "null").append(",")
                                .append("\"status\":\"").append(p.getStatus()).append("\"")
                                .append("}");
                        if (i < list.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    writeJson(resp, sb.toString());
                    break;
                }
                case "/app/admin/applications": {
                    List<sn.isi.immobilier.model.RentalApplication> list = applicationDao.findAll();
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < list.size(); i++) {
                        sn.isi.immobilier.model.RentalApplication a = list.get(i);
                        sb.append("{")
                                .append("\"id\":").append(a.getId()).append(",")
                                .append("\"unitId\":").append(a.getUnit() != null ? a.getUnit().getId() : "null").append(",")
                                .append("\"applicantId\":").append(a.getApplicant() != null ? a.getApplicant().getId() : "null").append(",")
                                .append("\"status\":\"").append(a.getStatus()).append("\",")
                                .append("\"createdAt\":\"").append(a.getCreatedAt() != null ? a.getCreatedAt().toString() : "").append("\"")
                                .append("}");
                        if (i < list.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    writeJson(resp, sb.toString());
                    break;
                }
                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeJson(resp, "{\"error\":\"Not found\"}");
            }
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    // --- POST actions (delete/update/terminate)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJson(resp, "{\"error\":\"Accès refusé\"}");
            return;
        }

        String path = req.getServletPath();
        try {
            switch (path) {
                case "/app/admin/users": {
                    String userAction = req.getParameter("action");
                    String userIdStr = req.getParameter("userId");
                    if (userAction != null && userIdStr != null) {
                        Long userId = Long.parseLong(userIdStr);
                        if ("delete".equals(userAction)) {
                            userDao.deleteById(userId);
                            writeJson(resp, "{\"message\":\"Utilisateur supprimé\"}");
                        } else if ("update".equals(userAction)) {
                            String fullName = req.getParameter("fullName");
                            String roleStr = req.getParameter("role");
                            User userToUpdate = userDao.findById(userId).orElse(null);
                            if (userToUpdate != null) {
                                userToUpdate.setFullName(fullName);
                                userToUpdate.setRole(sn.isi.immobilier.model.Enums.UserRole.valueOf(roleStr));
                                userDao.save(userToUpdate);
                                writeJson(resp, "{\"message\":\"Utilisateur mis à jour\"}");
                            } else {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                writeJson(resp, "{\"error\":\"Utilisateur introuvable\"}");
                            }
                        } else {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            writeJson(resp, "{\"error\":\"Action inconnue\"}");
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, "{\"error\":\"Paramètres manquants\"}");
                    }
                    break;
                }

                case "/app/admin/buildings": {
                    String buildingAction = req.getParameter("action");
                    String buildingIdStr = req.getParameter("buildingId");
                    if ("delete".equals(buildingAction) && buildingIdStr != null) {
                        buildingDao.deleteById(Long.parseLong(buildingIdStr));
                        writeJson(resp, "{\"message\":\"Immeuble supprimé\"}");
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, "{\"error\":\"Action inconnue ou paramètre manquant\"}");
                    }
                    break;
                }

                case "/app/admin/units": {
                    String unitAction = req.getParameter("action");
                    String unitIdStr = req.getParameter("unitId");
                    if ("delete".equals(unitAction) && unitIdStr != null) {
                        unitDao.deleteById(Long.parseLong(unitIdStr));
                        writeJson(resp, "{\"message\":\"Unité supprimée\"}");
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, "{\"error\":\"Action inconnue ou paramètre manquant\"}");
                    }
                    break;
                }

                case "/app/admin/leases": {
                    String leaseAction = req.getParameter("action");
                    String leaseIdStr = req.getParameter("leaseId");
                    if ("terminate".equals(leaseAction) && leaseIdStr != null) {
                        Lease lease = leaseDao.findById(Long.parseLong(leaseIdStr)).orElse(null);
                        if (lease != null) {
                            lease.setStatus(LeaseStatus.valueOf("TERMINATED"));
                            leaseDao.save(lease);
                            writeJson(resp, "{\"message\":\"Bail terminé\"}");
                        } else {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            writeJson(resp, "{\"error\":\"Bail introuvable\"}");
                        }
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        writeJson(resp, "{\"error\":\"Action inconnue ou paramètre manquant\"}");
                    }
                    break;
                }

                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeJson(resp, "{\"error\":\"Not found\"}");
            }
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
