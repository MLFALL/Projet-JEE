package sn.isi.immobilier.web;

import sn.isi.immobilier.model.Enums.PaymentStatus;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.Payment;
import sn.isi.immobilier.model.Enums.PaymentMethod;
import sn.isi.immobilier.model.User;
import sn.isi.immobilier.service.LeaseService;
import sn.isi.immobilier.service.PaymentService;
import sn.isi.immobilier.service.impl.LeaseServiceImpl;
import sn.isi.immobilier.service.impl.PaymentServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/payments", "/payments/pay"})
public class PaymentServlet extends HttpServlet {
    private PaymentService paymentService;
    private LeaseService leaseService;

    @Override
    public void init() {
        this.paymentService = new PaymentServiceImpl();
        this.leaseService = new LeaseServiceImpl();
    }

    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession(false);
        Long userId = s != null && s.getAttribute("userId") != null ? (Long) s.getAttribute("userId") : null;
        // show payments for leases of this user (service must implement)
        List<Payment> payments = paymentService.findDueByUser(userId); // implement findDueByUser or adapt
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/WEB-INF/jsp/payments/list.jsp").forward(req, resp);
    }*/
    /*@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        Long userId = session != null && session.getAttribute("userId") != null
                ? (Long) session.getAttribute("userId")
                : null;

        String role = session != null && session.getAttribute("role") != null
                ? (String) session.getAttribute("role")
                : null;

        if (userId == null || role == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Récupère les paiements selon le rôle
        List<Payment> payments = paymentService.findDueByUser(userId, role);
        req.setAttribute("payments", payments);
        req.setAttribute("role", role);
        req.setAttribute("userId", userId); // optionnel, peut être utilisé dans les conditions

        req.getRequestDispatcher("/WEB-INF/jsp/payments/list.jsp").forward(req, resp);
    }*/

    /*@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/payments/pay".equals(path)) {
            HttpSession session = req.getSession(false);
            Long userId = session != null && session.getAttribute("userId") != null
                    ? (Long) session.getAttribute("userId")
                    : null;

            if (userId == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            try {
                Long leaseId = Long.valueOf(req.getParameter("leaseId"));
                BigDecimal amount = new BigDecimal(req.getParameter("amount"));
                PaymentMethod method = PaymentMethod.valueOf(req.getParameter("method"));
                String reference = req.getParameter("reference");

                // Vérifier le bail
                Lease lease = leaseService.findById(leaseId);
                if (lease == null) throw new RuntimeException("Bail introuvable");
                if (!lease.getTenant().getId().equals(userId))
                    throw new IllegalAccessException("Vous n'êtes pas autorisé à payer ce bail");

                // Si aucun paiement trouvé → créer et persister
                Payment payment = paymentService.findDueByLease(leaseId)
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (payment == null) {
                    payment = new Payment();
                    payment.setLease(lease);
                    payment.setAmountDue(lease.getRentAmount());
                    payment.setAmountPaid(BigDecimal.ZERO);
                    payment.setStatus(PaymentStatus.PENDING);
                    payment.setDueDate(LocalDate.now().plusMonths(5));
                    payment = paymentService.createPayment(payment); // <- sauvegarde en DB
                }


                // Vérification du montant
                if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Montant invalide");
                if (payment.getAmountPaid().add(amount).compareTo(payment.getAmountDue()) > 0)
                    throw new IllegalArgumentException("Montant payé supérieur au montant dû");

                // Marquer le paiement
                paymentService.markPaid(payment.getId(), amount, method, reference);
                resp.sendRedirect(req.getContextPath() + "/payments");

            } catch (Exception ex) {
                // En cas d'erreur, renvoyer vers la liste avec le message
                req.setAttribute("error", ex.getMessage());
                doGet(req, resp);
            }
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }*/


    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    private User getCurrentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null ? (User) s.getAttribute("user") : null;
    }

    // GET -> list payments for user (uses paymentService.findDueByUser(userId, role))
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = getCurrentUser(req);
        if (u == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, "{\"error\":\"Not authenticated\"}");
            return;
        }
        String role = req.getSession().getAttribute("role") != null ? (String) req.getSession().getAttribute("role") : u.getRole().name();
        try {
            List<Payment> payments = paymentService.findDueByUser(u.getId(), role);
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < payments.size(); i++) {
                Payment p = payments.get(i);
                sb.append("{")
                        .append("\"id\":").append(p.getId()).append(",")
                        .append("\"leaseId\":").append(p.getLease() != null ? p.getLease().getId() : "null").append(",")
                        .append("\"dueDate\":\"").append(p.getDueDate() != null ? p.getDueDate().toString() : "").append("\",")
                        .append("\"amountDue\":").append(p.getAmountDue() != null ? p.getAmountDue() : "null").append(",")
                        .append("\"amountPaid\":").append(p.getAmountPaid() != null ? p.getAmountPaid() : "null").append(",")
                        .append("\"status\":\"").append(p.getStatus()).append("\"")
                        .append("}");
                if (i < payments.size() - 1) sb.append(",");
            }
            sb.append("]");
            writeJson(resp, sb.toString());
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeJson(resp, "{\"error\":\"" + escape(ex.getMessage()) + "\"}");
        }
    }

    // POST -> pay
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = getCurrentUser(req);
        if (u == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            writeJson(resp, "{\"error\":\"Not authenticated\"}");
            return;
        }

        String path = req.getServletPath();
        if (!"/payments/pay".equals(path)) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(resp, "{\"error\":\"Not found\"}");
            return;
        }

        try {
            Long leaseId = Long.valueOf(req.getParameter("leaseId"));
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            PaymentMethod method = PaymentMethod.valueOf(req.getParameter("method"));
            String reference = req.getParameter("reference");

            Lease lease = leaseService.findById(leaseId);
            if (lease == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writeJson(resp, "{\"error\":\"Bail introuvable\"}");
                return;
            }

            // Vérifier que l'utilisateur est bien le locataire du bail
            if (!lease.getTenant().getId().equals(u.getId())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                writeJson(resp, "{\"error\":\"Vous n'êtes pas autorisé à payer ce bail\"}");
                return;
            }

            Payment payment = paymentService.findDueByLease(leaseId)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (payment == null) {
                payment = new Payment();
                payment.setLease(lease);
                payment.setAmountDue(lease.getRentAmount());
                payment.setAmountPaid(BigDecimal.ZERO);
                payment.setStatus(PaymentStatus.PENDING);
                payment.setDueDate(LocalDate.now().plusMonths(1)); // logique: prochaine échéance
                payment = paymentService.createPayment(payment);
            }

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(resp, "{\"error\":\"Montant invalide\"}");
                return;
            }

            if (payment.getAmountPaid().add(amount).compareTo(payment.getAmountDue()) > 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(resp, "{\"error\":\"Montant payé supérieur au montant dû\"}");
                return;
            }

            Payment updated = paymentService.markPaid(payment.getId(), amount, method, reference);
            writeJson(resp, "{\"message\":\"Paiement enregistré\",\"paymentId\":" + updated.getId() + "}");
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
