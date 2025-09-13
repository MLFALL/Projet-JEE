package sn.isi.immobilier.web;

import sn.isi.immobilier.model.Enums.LeaseStatus;
import sn.isi.immobilier.model.Enums.PaymentStatus;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.Unit;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.*;
import sn.isi.immobilier.service.impl.LeaseServiceImpl;
import sn.isi.immobilier.service.impl.PaymentServiceImpl;
import sn.isi.immobilier.service.impl.UnitServiceImpl;
import sn.isi.immobilier.service.impl.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

// iText 7 importations corrigées
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

// Jakarta Mail importations
import jakarta.mail.MessagingException;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/leases", "/leases/terminate", "/leases/download", "/leases/create", "/leases/details"})
public class LeaseServlet extends HttpServlet {

    private LeaseService leaseService;
    private EmailService emailService;
    private UserService userService; // ajouter
    private UnitService unitService; // ajouter
    private PaymentService paymentService;

    @Override
    public void init() {
        this.leaseService = new LeaseServiceImpl();
        this.emailService = new EmailService();
        this.userService = new UserServiceImpl(); // créer impl si nécessaire
        this.unitService = new UnitServiceImpl(); // créer impl si nécessair
        this.paymentService = new PaymentServiceImpl();
    }

    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login"); // ou page de connexion
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equals(role);

        // Téléchargement d'un contrat PDF
        if ("/leases/download".equals(path)) {
            Long leaseId = Long.valueOf(req.getParameter("leaseId"));
            Lease lease = leaseService.findById(leaseId);

            if (lease == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Contrat non trouvé");
                return;
            }

            // Droits : locataire, propriétaire ou admin
            if (!lease.getTenant().getId().equals(userId) &&
                    !lease.getOwner().getId().equals(userId) &&
                    !isAdmin) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return;
            }

            String uploadDir = getServletContext().getRealPath("/uploads/contracts/");
            String pdfFileName = "contrat_" + leaseId + ".pdf";
            Path pdfPath = Path.of(uploadDir, pdfFileName);

            if (!Files.exists(pdfPath)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Fichier PDF introuvable");
                return;
            }

            resp.setContentType("application/pdf");
            resp.setHeader("Content-Disposition", "attachment; filename=" + pdfFileName);
            Files.copy(pdfPath, resp.getOutputStream());
            return;
        }
        if ("/leases/details".equals(path)) {
            Long leaseId = Long.valueOf(req.getParameter("leaseId"));
            Lease lease = leaseService.findById(leaseId);

            if (lease == null) {
                req.setAttribute("error", "Bail introuvable");
                req.getRequestDispatcher("/WEB-INF/jsp/leases/detail.jsp").forward(req, resp);
                return;
            }

            // Vérification droits : locataire, propriétaire ou admin
            if (!lease.getTenant().getId().equals(userId)
                    && !lease.getOwner().getId().equals(userId)
                    && !isAdmin) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return;
            }

            req.setAttribute("lease", lease);
            req.getRequestDispatcher("/WEB-INF/jsp/leases/detail.jsp").forward(req, resp);
            return;
        }


        // Liste des baux selon le rôle
        List<Lease> leases;

        if ("TENANT".equals(role)) {
            leases = leaseService.findByTenant(userId);
        } else if ("OWNER".equals(role)) {
            leases = leaseService.findByOwner(userId); // à créer dans LeaseService/Dao
        } else if (isAdmin) {
            leases = leaseService.findAll();
        } else {
            leases = List.of(); // aucun bail pour rôle inconnu
        }
        for (Lease l : leases) {
            boolean paid = paymentService.findDueByLease(l.getId())
                    .stream()
                    .allMatch(p -> p.getStatus() == PaymentStatus.PAID);            l.setPaymentPaid(paid); // ajouter un champ boolean `paymentPaid` dans Lease
            l.setPaymentPaid(paid);
        }
        req.setAttribute("leases", leases);
        req.getRequestDispatcher("/WEB-INF/jsp/leases/list.jsp").forward(req, resp);
    }*/

    /*@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equals(role);

        // Création d'un bail
        if ("/leases/create".equals(path)) {
            try {
                Long tenantId = Long.valueOf(req.getParameter("tenantId"));
                Long unitId = Long.valueOf(req.getParameter("unitId"));
                BigDecimal rentAmount = new BigDecimal(req.getParameter("rentAmount"));

// Récupérer les objets User et Unit depuis leur service respectif
                User tenant = userService.findById(tenantId);   // userService à injecter dans ton servlet
                Unit unit = unitService.findById(unitId);      // unitService à injecter dans ton servlet

                if (tenant == null || unit == null) {
                    throw new RuntimeException("Locataire ou unité introuvable");
                }

                Lease lease = new Lease();
                lease.setTenant(tenant);           // Objet User
                lease.setOwner(unit.getBuilding().getOwner()); // si tu veux définir le propriétaire automatiquement
                lease.setUnit(unit);               // Objet Unit
                lease.setRentAmount(rentAmount);
                lease.setDepositAmount(BigDecimal.ZERO); // ou selon logique
                lease.setStartDate(LocalDate.now());
                lease.setEndDate(LocalDate.now().plusMonths(12)); // exemple : 1 an
                lease.setStatus(LeaseStatus.ACTIVE);   // selon ton enum

                lease = leaseService.create(lease);    // Persistance via LeaseService

                // Génération PDF en mémoire
                ByteArrayOutputStream pdfOutput = new ByteArrayOutputStream();
                try (PdfWriter writer = new PdfWriter(pdfOutput);
                     PdfDocument pdfDoc = new PdfDocument(writer);
                     Document document = new Document(pdfDoc)) {

                    document.add(new Paragraph("Contrat de location"));
                    document.add(new Paragraph("Locataire : " + lease.getTenant().getFullName()));
                    document.add(new Paragraph("Immeuble : " + lease.getUnit().getBuilding().getName()));
                    document.add(new Paragraph("Unité : " + lease.getUnit().getUnitNumber()));
                    document.add(new Paragraph("Loyer : " + lease.getRentAmount() + " FCFA"));
                    document.add(new Paragraph("Date de début : " + lease.getStartDate()));
                    document.add(new Paragraph("Date de fin : " + lease.getEndDate().plusMonths(12)));
                }

                byte[] pdfBytes = pdfOutput.toByteArray();
                String pdfFileName = "contrat_" + lease.getId() + ".pdf";

                // Envoi mail automatique directement depuis la mémoire
                emailService.sendEmailWithAttachment(
                        lease.getTenant().getEmail(),
                        "Votre contrat de location",
                        "Bonjour, veuillez trouver ci-joint votre contrat de location.",
                        pdfBytes,
                        pdfFileName
                );

                // Sauvegarde PDF sur le serveur (optionnel, si tu veux garder une copie)
                String uploadDir = getServletContext().getRealPath("/uploads/contracts/");
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();
                Path pdfPath = Path.of(uploadDir, pdfFileName);
                Files.write(pdfPath, pdfBytes);

                resp.sendRedirect(req.getContextPath() + "/leases");

            } catch (Exception e) {
                req.setAttribute("error", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/jsp/leases/create.jsp").forward(req, resp);
            }
            return;
        }


        // Termination d'un bail
        if ("/leases/terminate".equals(path)) {
            Long leaseId = Long.valueOf(req.getParameter("leaseId"));
            Lease lease = leaseService.findById(leaseId);

            if (lease == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Bail non trouvé");
                return;
            }

            // Vérification droits : admin ou propriétaire du bail
            if (!lease.getTenant().getId().equals(userId) &&
                    !lease.getOwner().getId().equals(userId) &&
                    !isAdmin) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
                return;
            }

            leaseService.terminate(leaseId);
            resp.sendRedirect(req.getContextPath() + "/leases");
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }*/


    private User getCurrentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null ? (User) s.getAttribute("user") : null;
    }


    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    // GET: list or details
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, "{\"error\":\"Not authenticated\"}");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/leases/details".equals(path)) {
                Long leaseId = Long.valueOf(req.getParameter("leaseId"));
                Lease lease = leaseService.findById(leaseId);
                if (lease == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    writeJson(resp, "{\"error\":\"Bail introuvable\"}");
                    return;
                }
                // droits: tenant, owner, admin
                Long userId = currentUser.getId();
                boolean isAdmin = currentUser.getRole() == sn.isi.immobilier.model.Enums.UserRole.ADMIN;
                if (!isAdmin && !lease.getTenant().getId().equals(userId) && !lease.getOwner().getId().equals(userId)) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    writeJson(resp, "{\"error\":\"Accès refusé\"}");
                    return;
                }
                String json = "{"
                        + "\"id\":" + lease.getId() + ","
                        + "\"unitId\":" + (lease.getUnit() != null ? lease.getUnit().getId() : "null") + ","
                        + "\"tenantId\":" + (lease.getTenant() != null ? lease.getTenant().getId() : "null") + ","
                        + "\"ownerId\":" + (lease.getOwner() != null ? lease.getOwner().getId() : "null") + ","
                        + "\"startDate\":\"" + (lease.getStartDate() != null ? lease.getStartDate().toString() : "") + "\","
                        + "\"endDate\":\"" + (lease.getEndDate() != null ? lease.getEndDate().toString() : "") + "\","
                        + "\"rentAmount\":" + (lease.getRentAmount() != null ? lease.getRentAmount() : "null") + ","
                        + "\"status\":\"" + lease.getStatus() + "\""
                        + "}";
                writeJson(resp, json);
                return;
            }

            // default /leases -> list per role
            String role = currentUser.getRole().name();
            List<Lease> leases;
            if ("TENANT".equals(role)) leases = leaseService.findByTenant(currentUser.getId());
            else if ("OWNER".equals(role)) leases = leaseService.findByOwner(currentUser.getId());
            else leases = leaseService.findAll();

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < leases.size(); i++) {
                Lease l = leases.get(i);
                sb.append("{")
                        .append("\"id\":").append(l.getId()).append(",")
                        .append("\"unitId\":").append(l.getUnit() != null ? l.getUnit().getId() : "null").append(",")
                        .append("\"tenantId\":").append(l.getTenant() != null ? l.getTenant().getId() : "null").append(",")
                        .append("\"ownerId\":").append(l.getOwner() != null ? l.getOwner().getId() : "null").append(",")
                        .append("\"startDate\":\"").append(l.getStartDate() != null ? l.getStartDate().toString() : "").append("\",")
                        .append("\"endDate\":\"").append(l.getEndDate() != null ? l.getEndDate().toString() : "").append("\",")
                        .append("\"rentAmount\":").append(l.getRentAmount() != null ? l.getRentAmount() : "null").append(",")
                        .append("\"status\":\"").append(l.getStatus()).append("\"")
                        .append("}");
                if (i < leases.size() - 1) sb.append(",");
            }
            sb.append("]");
            writeJson(resp, sb.toString());
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    // POST: create
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, "{\"error\":\"Not authenticated\"}");
            return;
        }

        String path = req.getServletPath();
        try {
            if ("/leases/create".equals(path)) {
                Long tenantId = Long.valueOf(req.getParameter("tenantId"));
                Long unitId = Long.valueOf(req.getParameter("unitId"));
                BigDecimal rentAmount = new BigDecimal(req.getParameter("rentAmount"));

                User tenant = userService.findById(tenantId);
                sn.isi.immobilier.model.Unit unit = unitService.findById(unitId);

                if (tenant == null || unit == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writeJson(resp, "{\"error\":\"Locataire ou unité introuvable\"}");
                    return;
                }

                Lease lease = new Lease();
                lease.setTenant(tenant);
                lease.setOwner(unit.getBuilding().getOwner());
                lease.setUnit(unit);
                lease.setRentAmount(rentAmount);
                lease.setDepositAmount(BigDecimal.ZERO);
                lease.setStartDate(LocalDate.now());
                lease.setEndDate(LocalDate.now().plusMonths(12));
                lease.setStatus(sn.isi.immobilier.model.Enums.LeaseStatus.ACTIVE);

                Lease saved = leaseService.create(lease);

                String json = "{ \"message\":\"Bail créé\",\"leaseId\":" + saved.getId() + " }";
                writeJson(resp, json);
                return;
            }

            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(resp, "{\"error\":\"Not found\"}");
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
