<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<h2>Mes demandes de location</h2>

<c:choose>
    <c:when test="${user.role eq 'TENANT'}">
        <table class="table-applications">
            <thead>
            <tr>
                <th>ID</th>
                <th>Unite</th>
                <th>Message</th>
                <th>Statut</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="a" items="${applications}">
                <c:if test="${a.applicant.id eq user.id}">
                    <tr>
                        <td>${a.id}</td>
                        <td>${a.unit.unitNumber}</td>
                        <td>${a.message}</td>
                        <td class="${fn:toLowerCase(a.status.name())}">${a.status}</td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </c:when>

    <c:when test="${user.role eq 'OWNER'}">
        <table class="table-applications">
            <thead>
            <tr>
                <th>ID</th>
                <th>Unite</th>
                <th>Locataire</th>
                <th>Message</th>
                <th>Statut</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="a" items="${applications}">
                <c:if test="${a.unit.building.owner.id eq user.id}">
                    <tr>
                        <td>${a.id}</td>
                        <td>${a.unit.unitNumber}</td>
                        <td>${a.applicant.fullName}</td>
                        <td>${a.message}</td>
                        <td class="${fn:toLowerCase(a.status.name())}">${a.status}</td>
                        <td>
                            <c:if test="${a.status eq 'PENDING'}">
                                <form method="post" action="${pageContext.request.contextPath}/applications/approve" class="inline-form">
                                    <input type="hidden" name="applicationId" value="${a.id}"/>
                                    <button class="btn-approve" type="submit">Approuver</button>
                                </form>
                                <form method="post" action="${pageContext.request.contextPath}/applications/reject" class="inline-form">
                                    <input type="hidden" name="applicationId" value="${a.id}"/>
                                    <input type="text" name="reason" placeholder="Motif rejet"/>
                                    <button class="btn-reject" type="submit">Rejeter</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:if>
            </c:forEach>
            </tbody>
        </table>
    </c:when>

    <c:otherwise>
        <p>Veuillez vous connecter pour voir les demandes de location.</p>
    </c:otherwise>
</c:choose>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/applications.css"/>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
