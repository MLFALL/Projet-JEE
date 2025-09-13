package sn.isi.immobilier.web;

import sn.isi.immobilier.model.Building;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.BuildingService;
import sn.isi.immobilier.service.UnitService;
import sn.isi.immobilier.service.impl.BuildingServiceImpl;
import sn.isi.immobilier.service.impl.UnitServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(urlPatterns = {"/units", "/units/new", "/units/edit", "/units/save", "/units/delete"})
public class UnitsServlet extends HttpServlet {
    private UnitService unitService;
    private BuildingService buildingService;

    @Override
    public void init() {
        this.unitService = new UnitServiceImpl();
        this.buildingService = new BuildingServiceImpl();
    }

    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        User loggedInUser = (User) req.getSession().getAttribute("user");


        switch (path) {
            case "/units":
                String city = req.getParameter("city");
                Integer rooms = (req.getParameter("rooms") != null && !req.getParameter("rooms").isEmpty())
                        ? Integer.valueOf(req.getParameter("rooms")) : null;
                BigDecimal maxRent = (req.getParameter("maxRent") != null && !req.getParameter("maxRent").isEmpty())
                        ? new BigDecimal(req.getParameter("maxRent")) : null;

                List<Unit> list = unitService.listAvailable(city, rooms, maxRent);
                req.setAttribute("units", list);
                req.getRequestDispatcher("/WEB-INF/jsp/units/list.jsp").forward(req, resp);
                break;

            case "/units/new":
                Unit newUnit = new Unit();
                req.setAttribute("unit", newUnit);
                List<Building> availableBuildings = buildingService.findAvailableByOwner(loggedInUser.getId());
                req.setAttribute("availableBuildings", availableBuildings);
                req.getRequestDispatcher("/WEB-INF/jsp/units/form.jsp").forward(req, resp);
                break;

            case "/units/edit":
                String id = req.getParameter("id");
                if (id != null) {
                    Unit u = unitService.findById(Long.valueOf(id));
                    req.setAttribute("unit", u);
                    List<Building> buildings = buildingService.findAvailableByOwner(loggedInUser.getId());
                    if (u.getBuilding() != null && buildings.stream().noneMatch(b -> b.getId().equals(u.getBuilding().getId()))) {
                        buildings.add(u.getBuilding());
                    }
                    req.setAttribute("availableBuildings", buildings);
                }
                req.getRequestDispatcher("/WEB-INF/jsp/units/form.jsp").forward(req, resp);
                break;

            case "/units/delete":
                String deleteId = req.getParameter("id");
                if (deleteId != null) unitService.delete(Long.valueOf(deleteId));
                resp.sendRedirect(req.getContextPath() + "/units");
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }*/

    /*@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String unitNumber = req.getParameter("unitNumber");
        String roomsStr = req.getParameter("rooms");
        String rentStr = req.getParameter("rentAmount");
        String buildingIdStr = req.getParameter("buildingId");

        try {
            // Vérifications des champs
            if (unitNumber == null || unitNumber.isBlank()) throw new IllegalArgumentException("Numéro d'unité obligatoire");
            if (roomsStr == null || roomsStr.isBlank()) throw new IllegalArgumentException("Nombre de pièces obligatoire");
            if (rentStr == null || rentStr.isBlank()) throw new IllegalArgumentException("Loyer obligatoire");
            if (buildingIdStr == null || buildingIdStr.isBlank()) throw new IllegalArgumentException("Bâtiment obligatoire");



            int rooms = Integer.parseInt(roomsStr);
            BigDecimal rent = new BigDecimal(rentStr);
            Long buildingId = Long.valueOf(buildingIdStr);

            if (id == null || id.isEmpty()) {
                unitService.create(buildingId, unitNumber, rooms, rent);
            } else {
                Unit u = unitService.findById(Long.valueOf(id));
                if (u == null) throw new IllegalArgumentException("Unité introuvable pour modification");
                u.setUnitNumber(unitNumber);
                u.setRooms(rooms);
                u.setRentAmount(rent);
                u.setBuilding(buildingService.findById(buildingId));
                unitService.update(u.getId(), u);
            }

            resp.sendRedirect(req.getContextPath() + "/units");

        } catch (Exception ex) {
            User loggedInUser = (User) req.getSession().getAttribute("user");
            Unit unit = id != null ? unitService.findById(Long.valueOf(id)) : new Unit();
            if (buildingIdStr != null && !buildingIdStr.isEmpty()) {
                unit.setBuilding(buildingService.findById(Long.valueOf(buildingIdStr)));
            }
            req.setAttribute("unit", unit);
            List<Building> availableBuildings = buildingService.findAvailableByOwner(loggedInUser.getId());
            req.setAttribute("availableBuildings", availableBuildings);
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/units/form.jsp").forward(req, resp);
        }
    }*/



    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    // GET: list or detail
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            if ("/units".equals(path)) {
                String city = req.getParameter("city");
                Integer rooms = (req.getParameter("rooms") != null && !req.getParameter("rooms").isEmpty())
                        ? Integer.valueOf(req.getParameter("rooms")) : null;
                BigDecimal maxRent = (req.getParameter("maxRent") != null && !req.getParameter("maxRent").isEmpty())
                        ? new BigDecimal(req.getParameter("maxRent")) : null;

                List<Unit> list = unitService.listAvailable(city, rooms, maxRent);

                StringBuilder sb = new StringBuilder("[");
                for (int i = 0; i < list.size(); i++) {
                    Unit u = list.get(i);
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
                return;
            }

            if ("/units/edit".equals(path)) {
                String id = req.getParameter("id");
                if (id == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, "{\"error\":\"ID requis\"}");
                    return;
                }
                Unit u = unitService.findById(Long.valueOf(id));
                if (u == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeJson(resp, "{\"error\":\"Unité introuvable\"}");
                    return;
                }
                String json = "{"
                        + "\"id\":" + u.getId() + ","
                        + "\"unitNumber\":\"" + escape(u.getUnitNumber()) + "\","
                        + "\"rooms\":" + u.getRooms() + ","
                        + "\"rentAmount\":" + (u.getRentAmount() != null ? u.getRentAmount() : "null") + ","
                        + "\"status\":\"" + u.getStatus() + "\","
                        + "\"buildingId\":" + (u.getBuilding() != null ? u.getBuilding().getId() : "null")
                        + "}";
                writeJson(resp, json);
                return;
            }

            if ("/units/delete".equals(path)) {
                String deleteId = req.getParameter("id");
                if (deleteId != null) {
                    unitService.delete(Long.valueOf(deleteId));
                    writeJson(resp, "{\"message\":\"Unité supprimée\"}");
                    return;
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, "{\"error\":\"ID requis\"}");
                    return;
                }
            }

            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(resp, "{\"error\":\"Not found\"}");
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    // POST: create or update
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String id = req.getParameter("id");
            String unitNumber = req.getParameter("unitNumber");
            String roomsStr = req.getParameter("rooms");
            String rentStr = req.getParameter("rentAmount");
            String buildingIdStr = req.getParameter("buildingId");

            if (unitNumber == null || unitNumber.isBlank()) throw new IllegalArgumentException("Numéro d'unité obligatoire");
            if (roomsStr == null || roomsStr.isBlank()) throw new IllegalArgumentException("Nombre de pièces obligatoire");
            if (rentStr == null || rentStr.isBlank()) throw new IllegalArgumentException("Loyer obligatoire");
            if (buildingIdStr == null || buildingIdStr.isBlank()) throw new IllegalArgumentException("Bâtiment obligatoire");

            int rooms = Integer.parseInt(roomsStr);
            BigDecimal rent = new BigDecimal(rentStr);
            Long buildingId = Long.valueOf(buildingIdStr);

            if (id == null || id.isEmpty()) {
                // create
                Unit created = unitService.create(buildingId, unitNumber, rooms, rent);
                writeJson(resp, "{ \"message\":\"Unité créée\",\"id\":" + created.getId() + " }");
            } else {
                Unit u = unitService.findById(Long.valueOf(id));
                if (u == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeJson(resp, "{\"error\":\"Unité introuvable\"}");
                    return;
                }
                u.setUnitNumber(unitNumber);
                u.setRooms(rooms);
                u.setRentAmount(rent);
                Building b = buildingService.findById(buildingId);
                u.setBuilding(b);
                Unit updated = unitService.update(u.getId(), u);
                writeJson(resp, "{ \"message\":\"Unité mise à jour\",\"id\":" + updated.getId() + " }");
            }
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
