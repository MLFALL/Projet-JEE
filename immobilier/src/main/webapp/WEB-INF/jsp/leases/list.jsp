<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="leases-container">
    <c:choose>
        <c:when test="${not empty user}">
            <h2>Mes Baux</h2>
            <table class="leases-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Unite</th>
                    <th>Loyer</th>
                    <th>Statut Bail</th>
                    <th>Statut Paiement</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="l" items="${leases}">
                    <tr>
                        <td>${l.id}</td>
                        <td>${l.unit.unitNumber}</td>
                        <td>${l.rentAmount} FCFA</td>
                        <td>${l.status}</td>
                        <td>
                            <c:choose>
                                <c:when test="${l.paymentPaid}">
                                    PAYE
                                </c:when>
                                <c:otherwise>
                                    EN ATTENTE
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${(user.role eq 'OWNER' or user.role eq 'ADMIN') and l.status eq 'ACTIVE'}">
                                <form method="post" action="${pageContext.request.contextPath}/leases/terminate" class="inline-form">
                                    <input type="hidden" name="leaseId" value="${l.id}"/>
                                    <button type="submit" class="btn-terminate">Resilier</button>
                                </form>
                            </c:if>

                            <c:if test="${user.role eq 'TENANT' and not l.paymentPaid}">
                                <form method="post" action="${pageContext.request.contextPath}/payments/pay" class="inline-form">
                                    <input type="hidden" name="leaseId" value="${l.id}"/>
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
                            <a href="${pageContext.request.contextPath}/leases/details?leaseId=${l.id}" class="btn-details">Voir détails</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:otherwise>
            <p>Veuillez vous connecter pour voir vos baux.</p>
        </c:otherwise>
    </c:choose>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tables.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
