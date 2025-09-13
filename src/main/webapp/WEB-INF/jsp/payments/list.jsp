<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="payments-container">
    <h2>Mes Paiements</h2>

    <c:choose>
        <c:when test="${role eq 'TENANT'}">
            <table class="payments-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Loyer du</th>
                    <th>Paye</th>
                    <th>Statut</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${payments}">
                    <c:if test="${p.lease.tenant.id eq userId}">
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.amountDue} FCFA</td>
                            <td>${p.amountPaid} FCFA</td>
                            <td>${p.status}</td>
                            <td>
                                <c:if test="${p.status ne 'PAID'}">
                                    <form method="post" action="${pageContext.request.contextPath}/payments/pay" class="payment-form">
                                        <input type="hidden" name="leaseId" value="${p.lease.id}" />
                                        <input type="number" name="amount" placeholder="Montant" required/>
                                        <select name="method" required>
                                            <option value="">-- Choisir méthode --</option>
                                            <option value="CASH">Cash</option>
                                            <option value="BANK_TRANSFER">Virement</option>
                                            <option value="MOBILE_MONEY">Mobile Money</option>
                                        </select>
                                        <input type="text" name="reference" placeholder="Reference (optionnel)"/>
                                        <button type="submit" class="btn-pay">Payer</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:when test="${role eq 'OWNER'}">
            <table class="payments-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Loyer dû</th>
                    <th>Paye</th>
                    <th>Statut</th>
                    <th>Locataire</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="p" items="${payments}">
                    <c:if test="${p.lease.unit.owner.id eq userId}">
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.amountDue} FCFA</td>
                            <td>${p.amountPaid} FCFA</td>
                            <td>${p.status}</td>
                            <td>${p.lease.tenant.fullName}</td>
                        </tr>
                    </c:if>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:otherwise>
            <p>Veuillez vous connecter pour voir les paiements.</p>
        </c:otherwise>
    </c:choose>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tables.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
