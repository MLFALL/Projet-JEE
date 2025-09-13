package sn.isi.immobilier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Object user = (session != null) ? session.getAttribute("user") : null;

        request.setAttribute("user", user);

        // Renvoie vers la JSP index.jsp qui utilise header/footer
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
