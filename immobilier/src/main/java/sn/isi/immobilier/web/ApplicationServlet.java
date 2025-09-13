package sn.isi.immobilier.web;

import sn.isi.immobilier.model.Enums.UserRole;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.RentalApplication;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.ApplicationService;
import sn.isi.immobilier.service.EmailService;
import sn.isi.immobilier.service.UnitService;
import sn.isi.immobilier.service.impl.ApplicationServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import sn.isi.immobilier.service.impl.UnitServiceImpl;
import sn.isi.immobilier.util.LeaseUtil;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/applications", "/applications/submit", "/applications/approve", "/applications/reject"})
public class ApplicationServlet extends HttpServlet {
    private ApplicationService appService;
    private UnitService unitService;

    @Override
    public void init() {
        this.appService = new ApplicationServiceImpl();
        this.unitService = new UnitServiceImpl();
    }

    private Long getUserId(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("userId") != null ? (Long) s.getAttribute("userId") : null;
    }

    private String getUserRole(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("role") != null ? (String) s.getAttribute("role") : null;
    }
    private User getLoggedUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null ? (User) session.getAttribute("user") : null;
    }


    /*protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long userId = getUserId(req);
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<RentalApplication> list = appService.listByApplicant(userId);
        req.setAttribute("applications", list);
        req.getRequestDispatcher("/WEB-INF/jsp/applications/list.jsp").forward(req, resp);
    }*/

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getLoggedUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        if ("/applications/submit".equals(path)) {
            // Vérification que le rôle est bien TENANT
            if (currentUser.getRole() != UserRole.TENANT) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous ne pouvez pas faire cette action");
                return;
            }

            try {
                Long unitId = Long.valueOf(req.getParameter("unitId"));
                Unit unit = unitService.findById(unitId);
                if (unit == null) throw new IllegalArgumentException("Unité introuvable");

                // Création de la demande
                appService.submit(currentUser.getId(), unitId, "Demande automatique");
                req.getSession().setAttribute("success", "Demande envoyée avec succès !");
            } catch (Exception e) {
                req.getSession().setAttribute("error", e.getMessage());
            }

            resp.sendRedirect(req.getContextPath() + "/applications");
            return;
        }

        // Liste des demandes selon le rôle
        List<RentalApplication> list;
        if (currentUser.getRole() == UserRole.TENANT) {
            list = appService.listByApplicant(currentUser.getId());
        } else if (currentUser.getRole() == UserRole.OWNER) {
            list = appService.listByOwnerUnits(currentUser.getId());
        } else { // ADMIN
            list = appService.listAll(); // à ajouter dans ApplicationServiceImpl si nécessaire
        }
        req.setAttribute("applications", list);
        req.getRequestDispatcher("/WEB-INF/jsp/applications/list.jsp").forward(req, resp);
    }


    /*protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession s = req.getSession(false);
        Long userId = getUserId(req);
        User currentUser = s != null ? (User) s.getAttribute("user") : null;

        if (userId == null || currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            if ("/applications/submit".equals(path)) {
                Long unitId = Long.valueOf(req.getParameter("unitId"));
                String message = req.getParameter("message");
                if (message == null || message.isBlank()) throw new IllegalArgumentException("Message obligatoire");
                appService.submit(userId, unitId, message);
            }
            else if ("/applications/approve".equals(path) || "/applications/reject".equals(path)) {
                String role = getUserRole(req);
                if (!"ADMIN".equals(role) && !"OWNER".equals(role)) {
                    throw new IllegalAccessException("Vous n'avez pas les droits pour effectuer cette action");
                }

                Long appId = Long.valueOf(req.getParameter("applicationId"));
                if ("/applications/approve".equals(path)) {
                    appService.approve(appId, userId);
                } else {
                    String reason = req.getParameter("reason");
                    if (reason == null || reason.isBlank()) reason = "Aucune raison spécifiée";
                    appService.reject(appId, userId, reason);
                }
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/applications");
        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/applications/list.jsp").forward(req, resp);
        }
    }*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getLoggedUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();

        try {
            if ("/applications/submit".equals(path)) {
                // Formulaire POST pour demander une unité
                if (currentUser.getRole() != UserRole.TENANT) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous ne pouvez pas faire cette action");
                    return;
                }


                Long unitId = Long.valueOf(req.getParameter("unitId"));
                String message = req.getParameter("message");
                if (message == null || message.isBlank()) message = "Demande automatique";

                Unit unit = unitService.findById(unitId);
                if (unit == null) throw new IllegalArgumentException("Unité introuvable");

                appService.submit(currentUser.getId(), unitId, message);
                req.getSession().setAttribute("success", "Demande envoyée avec succès !");
                resp.sendRedirect(req.getContextPath() + "/applications");
                return;
            }

            // Actions admin/propriétaire (approver/rejecter)
            else if ("/applications/approve".equals(path) || "/applications/reject".equals(path)) {
                UserRole role = currentUser.getRole();
                if (role != UserRole.OWNER && role != UserRole.ADMIN) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous n'avez pas les droits");
                    return;
                }


                Long appId = Long.valueOf(req.getParameter("applicationId"));
                RentalApplication app;

                if ("/applications/approve".equals(path)) {
                    // Approbation
                    System.out.println("Approbation demandée pour applicationId: " + appId);
                    app = appService.approve(appId, currentUser.getId());
                    System.out.println("Application approuvée: " + app.getId());

                    // Création temporaire du Lease pour générer PDF + email
                    Lease lease = new Lease();
                    lease.setUnit(app.getUnit());
                    lease.setTenant(app.getApplicant());
                    lease.setOwner(app.getUnit().getBuilding().getOwner());
                    lease.setStartDate(java.time.LocalDate.now());
                    lease.setRentAmount(app.getUnit().getRentAmount());
                    lease.setStatus(sn.isi.immobilier.model.Enums.LeaseStatus.ACTIVE);

                    try {
                        System.out.println("Création du PDF et envoi du mail...");
                        LeaseUtil.generatePdfAndSendEmail(
                                lease,
                                getServletContext().getRealPath("/"),
                                new EmailService()
                        );
                        System.out.println("=== PDF généré et mail tenté ===");

                        req.getSession().setAttribute("success", "Demande approuvée et email envoyé !");
                    } catch (Exception e) {
                        // Même si le mail échoue, la demande reste approuvée
                        req.getSession().setAttribute("warning", "Demande approuvée mais l'email n'a pas pu être envoyé : " + e.getMessage());
                    }
                } else {
                    // Rejet
                    String reason = req.getParameter("reason");
                    if (reason == null || reason.isBlank()) reason = "Aucune raison spécifiée";
                    app = appService.reject(appId, currentUser.getId(), reason);
                    System.out.println("Rejet demandé pour applicationId: " + appId + " avec raison: " + reason);
                    req.getSession().setAttribute("success", "Demande rejetée avec succès !");
                }

                // Recharge la liste des applications pour le propriétaire
                List<RentalApplication> list = appService.listByOwnerUnits(currentUser.getId());
                req.setAttribute("applications", list);

                // Affiche la page au lieu de rediriger pour que la liste soit visible immédiatement
                req.getRequestDispatcher("/WEB-INF/jsp/applications/list.jsp").forward(req, resp);
                return;
            }

            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/applications/list.jsp").forward(req, resp);
        }
    }
}
