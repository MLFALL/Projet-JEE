package sn.isi.immobilier.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.User;

import java.io.IOException;

public class RoleFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // Récupère l'utilisateur dans la session
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Non connecté → redirection vers login
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserRole roleEnum = user.getRole();

        String role = roleEnum.name(); // ADMIN, OWNER, TENANT
        String path = req.getRequestURI();

        // Vérification des rôles pour chaque route
        if (path.startsWith(req.getContextPath() + "/app/admin") && !"ADMIN".equals(role)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        if (path.startsWith(req.getContextPath() + "/app/owner") && !"OWNER".equals(role)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        if (path.startsWith(req.getContextPath() + "/app/tenant") && !"TENANT".equals(role)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        // Tout est OK → continuer
        chain.doFilter(request, response);
    }

}
