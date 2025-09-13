<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="lease-details-container">
    <c:choose>

        <c:when test="${not empty user}">
            <h2>Details du Bail</h2>

            <c:if test="${not empty lease}">
                <table class="lease-details-table">
                    <tr>
                        <th>ID</th>
                        <td>${lease.id}</td>
                    </tr>
                    <tr>
                        <th>Unité</th>
                        <td>${lease.unit.unitNumber}</td>
                    </tr>
                    <tr>
                        <th>Loyer</th>
                        <td>${lease.rentAmount} FCFA</td>
                    </tr>
                    <tr>
                        <th>Locataire</th>
                        <td>${lease.tenant.fullName}</td>
                    </tr>
                    <tr>
                        <th>Proprietaire</th>
                        <td>${lease.owner.fullName}</td>
                    </tr>
                    <tr>
                        <th>Statut Bail</th>
                        <td>${lease.status}</td>
                    </tr>
                    <tr>
                        <th>Date de début</th>
                        <td>${lease.startDate}</td>
                    </tr>
                    <tr>
                        <th>Date de fin</th>
                        <td>${lease.endDate}</td>
                    </tr>
                    <tr>
                        <th>Statut Paiement</th>
                        <td>
                            <c:choose>
                                <c:when test="${lease.paymentPaid}">PAYE</c:when>
                                <c:otherwise>EN ATTENTE</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </table>

                <c:if test="${(user.role eq 'OWNER' or user.role eq 'ADMIN') and lease.status eq 'ACTIVE'}">
                    <form method="post" action="${pageContext.request.contextPath}/leases/terminate" class="inline-form">
                        <input type="hidden" name="leaseId" value="${lease.id}"/>
                        <button type="submit" class="btn-terminate">Resilier ce bail</button>
                    </form>
                </c:if>

                <c:if test="${user.role eq 'TENANT' and not lease.paymentPaid}">
                    <form method="post" action="${pageContext.request.contextPath}/payments/pay" class="inline-form">
                        <input type="hidden" name="leaseId" value="${lease.id}"/>
                        <input type="number" name="amount" placeholder="Montant" required/>
                        <select name="method" required>
                            <option value="CASH">Cash</option>
                            <option value="BANK_TRANSFER">Virement bancaire</option>
                            <option value="MOBILE_MONEY">Mobile Money</option>
                        </select>
                        <input type="text" name="reference" placeholder="Reference"/>
                        <button type="submit" class="btn-pay">Payer</button>
                    </form>
                </c:if>

            </c:if>

            <c:if test="${empty lease}">
                <p>Bail introuvable.</p>
            </c:if>

        </c:when>

        <c:otherwise>
            <p>Veuillez vous connecter pour voir les détails du bail.</p>
        </c:otherwise>
    </c:choose>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tables.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
