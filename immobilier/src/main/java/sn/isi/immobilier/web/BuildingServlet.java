package sn.isi.immobilier.web;

import sn.isi.immobilier.model.Building;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.BuildingService;
import sn.isi.immobilier.service.impl.BuildingServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {
        "/buildings", "/buildings/new", "/buildings/edit", "/buildings/save", "/buildings/delete"
})
public class BuildingServlet extends HttpServlet {
    private BuildingService buildingService;

    @Override
    public void init() {
        this.buildingService = new BuildingServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        switch (path) {
            case "/buildings":
                req.setAttribute("buildings", buildingService.findAll());
                req.getRequestDispatcher("/WEB-INF/jsp/buildings/list.jsp").forward(req, resp);
                break;

            case "/buildings/new":
                req.setAttribute("building", new Building());
                req.getRequestDispatcher("/WEB-INF/jsp/buildings/form.jsp").forward(req, resp);
                break;

            case "/buildings/edit":
                String idEdit = req.getParameter("id");
                Building bEdit = new Building();
                if (idEdit != null) {
                    try {
                        Long buildingId = Long.valueOf(idEdit);
                        Building found = buildingService.findById(buildingId);
                        if (found != null) bEdit = found;
                        else {
                            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Immeuble introuvable");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
                        return;
                    }
                }
                req.setAttribute("building", bEdit);
                req.getRequestDispatcher("/WEB-INF/jsp/buildings/form.jsp").forward(req, resp);
                break;

            case "/buildings/delete":
                String idDel = req.getParameter("id");
                if (idDel != null) {
                    try {
                        buildingService.delete(Long.valueOf(idDel));
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide");
                        return;
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/buildings");
                break;

            default:
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

   /* @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String street = req.getParameter("street");
        String city = req.getParameter("city");
        String description = req.getParameter("description");

        Building b = (id == null || id.isEmpty()) ? new Building() : buildingService.findById(Long.valueOf(id));

        try {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Nom obligatoire");
            if (street == null || street.isBlank()) throw new IllegalArgumentException("Rue obligatoire");
            if (city == null || city.isBlank()) throw new IllegalArgumentException("Ville obligatoire");

            b.setName(name);
            b.setStreet(street);
            b.setCity(city);
            b.setDescription(description);

            buildingService.createOrUpdate(b);
            resp.sendRedirect(req.getContextPath() + "/buildings");

        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("building", b);
            req.getRequestDispatcher("/WEB-INF/jsp/buildings/form.jsp").forward(req, resp);
        }
    }*/
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
           throws ServletException, IOException {

       String id = req.getParameter("id");
       String name = req.getParameter("name");
       String street = req.getParameter("street");
       String city = req.getParameter("city");
       String description = req.getParameter("description");
       String region = req.getParameter("region");
       String postalCode = req.getParameter("postalCode");
       String country = req.getParameter("country");
       String amenities = req.getParameter("amenities");

       Building b = (id == null || id.isEmpty()) ? new Building() : buildingService.findById(Long.valueOf(id));

       try {
           if (name == null || name.isBlank()) throw new IllegalArgumentException("Nom obligatoire");
           if (street == null || street.isBlank()) throw new IllegalArgumentException("Rue obligatoire");
           if (city == null || city.isBlank()) throw new IllegalArgumentException("Ville obligatoire");

           b.setName(name);
           b.setStreet(street);
           b.setCity(city);
           b.setDescription(description);
           b.setRegion(region);
           b.setPostalCode(postalCode);
           b.setCountry(country);
           b.setAmenities(amenities);

           // Assigner le propriétaire si c’est un nouvel immeuble
           if (b.getOwner() == null) {
               User loggedInUser = (User) req.getSession().getAttribute("user");
               if (loggedInUser != null) b.setOwner(loggedInUser);
           }

           buildingService.createOrUpdate(b);

           resp.sendRedirect(req.getContextPath() + "/buildings");

       } catch (Exception ex) {
           req.setAttribute("error", ex.getMessage());
           req.setAttribute("building", b);
           req.getRequestDispatcher("/WEB-INF/jsp/buildings/form.jsp").forward(req, resp);
       }
   }

}
