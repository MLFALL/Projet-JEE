package sn.isi.immobilier.web;

import sn.isi.immobilier.model.Enums.PaymentStatus;
import sn.isi.immobilier.model.Lease;
import sn.isi.immobilier.model.Payment;
import sn.isi.immobilier.model.Enums.PaymentMethod;
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
    @Override
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
    }

    @Override
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
    }

}
