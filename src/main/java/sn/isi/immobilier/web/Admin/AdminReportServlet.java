package sn.isi.immobilier.web.Admin;

import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.AdminService;
import sn.isi.immobilier.service.impl.AdminServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/app/admin/reports")
public class AdminReportServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() {
        this.adminService = new AdminServiceImpl();
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (session != null) ? (User) session.getAttribute("user") : null;
    }

    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);

        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        // Récupère les statistiques via le service admin
        req.setAttribute("stats", adminService.getStatistics());
        req.getRequestDispatcher("/WEB-INF/jsp/admin/reports.jsp").forward(req, resp);
    }*/


    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            writeJson(resp, "{\"error\":\"Accès refusé\"}");
            return;
        }

        try {
            Map<String, Long> stats = adminService.getStatistics();
            StringBuilder sb = new StringBuilder("{");
            int i = 0;
            for (Map.Entry<String, Long> e : stats.entrySet()) {
                sb.append("\"").append(e.getKey()).append("\":").append(e.getValue());
                if (i++ < stats.size() - 1) sb.append(",");
            }
            sb.append("}");
            writeJson(resp, sb.toString());
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
