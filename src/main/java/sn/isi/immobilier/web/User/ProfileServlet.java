package sn.isi.immobilier.web.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.UserService;
import sn.isi.immobilier.service.impl.UserServiceImpl;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() {
        this.userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("user");
        req.setAttribute("user", currentUser);
        req.getRequestDispatcher("/WEB-INF/jsp/users/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User currentUser = (User) req.getSession().getAttribute("user");
        String fullName = req.getParameter("fullName");
        String password = req.getParameter("password");

        try {
            if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Nom complet obligatoire");
            currentUser.setFullName(fullName);

            if (password != null && !password.isEmpty()) {
                if (password.length() < 6) throw new IllegalArgumentException("Le mot de passe doit avoir au moins 6 caractères");
                String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
                currentUser.setPasswordHash(hashed);
            }

            userService.update(currentUser);
            req.getSession().setAttribute("user", currentUser);
            resp.sendRedirect(req.getContextPath() + "/profile");

        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("user", currentUser);
            req.getRequestDispatcher("/WEB-INF/jsp/users/profile.jsp").forward(req, resp);
        }
    }

}