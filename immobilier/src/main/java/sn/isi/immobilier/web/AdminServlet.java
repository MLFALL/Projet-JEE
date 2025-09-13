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

@WebServlet(urlPatterns = {
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

    // --- GET ---
    @Override
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
    }

    // --- POST ---
    @Override
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
    }
}
