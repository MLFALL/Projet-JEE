package sn.isi.immobilier.web;


import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.AuthService;
import sn.isi.immobilier.service.impl.AuthServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() { this.authService = new AuthServiceImpl(); }

    // Récupérer l'utilisateur connecté en toute sécurité
    private User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object obj = session.getAttribute("user");
        if (obj instanceof User) return (User) obj;
        return null;
    }

    // Récupérer l'ID de l'utilisateur
    private Long getCurrentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object obj = session.getAttribute("userId");
        if (obj instanceof Long) return (Long) obj;
        return null;
    }

    // Récupérer le rôle de l'utilisateur (ADMIN, OWNER, TENANT)
    private String getCurrentUserRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        Object obj = session.getAttribute("role");
        if (obj instanceof String) return (String) obj;
        return null;
    }

    @Override protected void doGet(HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        // redirection vers  jsp
        /*try {
            User user = authService.login(email, password);
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole().name());
            //resp.sendRedirect(req.getContextPath() + "/units");
            // Redirection selon le rôle
            switch (user.getRole()) {
                case ADMIN:
                    resp.sendRedirect(req.getContextPath() + "/app/admin/dashboard");
                    break;
                case OWNER:
                    resp.sendRedirect(req.getContextPath() + "/buildings");
                    break;
                case TENANT:
                    resp.sendRedirect(req.getContextPath() + "/units");
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/"); // fallback
                    break;
            }
        } catch (RuntimeException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
        }*/
        //redirection vers json
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            User user = authService.login(email, password);

            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("role", user.getRole().name());

            // Réponse JSON
            String json = "{"
                    + "\"status\":\"success\","
                    + "\"message\":\"Login réussi\","
                    + "\"userId\":" + user.getId() + ","
                    + "\"role\":\"" + user.getRole().name() + "\","
                    + "\"fullName\":\"" + user.getFullName() + "\""
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