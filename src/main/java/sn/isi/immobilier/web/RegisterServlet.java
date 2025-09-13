package sn.isi.immobilier.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.AuthService;
import sn.isi.immobilier.service.impl.AuthServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private AuthService authService;

    @Override public void init() { authService = new AuthServiceImpl(); }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String fullname = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String roleParam = req.getParameter("role"); // "TENANT" or "OWNER"
        UserRole role = UserRole.valueOf(roleParam != null ? roleParam : "TENANT");
        //jsp
        /*try {
            // Création du compte utilisateur
            authService.register(email, fullname, phone, password, role);

            // Après inscription → redirection vers login
            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } catch (RuntimeException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/auth/register.jsp").forward(req, resp);
        }*/
        //json
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            authService.register(email, fullname, phone, password, role);

            String json = "{"
                    + "\"status\":\"success\","
                    + "\"message\":\"Inscription réussie\""
                    + "}";
            resp.getWriter().write(json);

        } catch (RuntimeException ex) {
            String json = "{"
                    + "\"status\":\"error\","
                    + "\"message\":\"" + ex.getMessage() + "\""
                    + "}";
            resp.getWriter().write(json);
        }
    }
}