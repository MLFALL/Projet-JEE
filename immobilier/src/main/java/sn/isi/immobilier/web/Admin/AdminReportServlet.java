package sn.isi.immobilier.web.Admin;

import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.AdminService;
import sn.isi.immobilier.service.impl.AdminServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);

        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        // Récupère les statistiques via le service admin
        req.setAttribute("stats", adminService.getStatistics());
        req.getRequestDispatcher("/WEB-INF/jsp/admin/reports.jsp").forward(req, resp);
    }
}
