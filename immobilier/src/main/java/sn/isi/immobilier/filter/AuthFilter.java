package sn.isi.immobilier.filter;

import jakarta.servlet.*; import jakarta.servlet.http.*; import java.io.IOException;
public class AuthFilter implements Filter {
    public void doFilter(ServletRequest r, ServletResponse s, FilterChain c)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) r;
        HttpServletResponse res = (HttpServletResponse) s;
        HttpSession session = req.getSession(false);
        boolean logged = session != null && session.getAttribute("userId") != null;
        if (!logged) { res.sendRedirect(req.getContextPath()+"/login"); return; }
        c.doFilter(r, s);
    }
}
