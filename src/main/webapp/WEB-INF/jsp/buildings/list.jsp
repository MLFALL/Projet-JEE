<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="buildings-container">
    <h2>Immeubles</h2>

    <c:choose>

        <c:when test="${user.role eq 'OWNER'}">
            <a href="${pageContext.request.contextPath}/buildings/new" class="btn-add">Ajouter un immeuble</a>
            <table class="table-buildings">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Adresse complete</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="b" items="${buildings}">
                    <tr>
                        <td>${b.id}</td>
                        <td>${b.name}</td>
                        <td>
                                ${b.street}, ${b.city}
                            <c:if test="${not empty b.region}">, ${b.region}</c:if>
                            <c:if test="${not empty b.postalCode}">, ${b.postalCode}</c:if>
                            <c:if test="${not empty b.country}">, ${b.country}</c:if>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/buildings/edit?id=${b.id}" class="btn-edit">Modifier</a>
                            <a href="${pageContext.request.contextPath}/buildings/delete?id=${b.id}"
                               class="btn-delete"
                               onclick="return confirm('Etes-vous sur de vouloir supprimer cet immeuble ?');">Supprimer</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:when test="${user.role eq 'TENANT'}">
            <table class="table-buildings">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Adresse complete</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="b" items="${buildings}">
                    <tr>
                        <td>${b.id}</td>
                        <td>${b.name}</td>
                        <td>
                                ${b.street}, ${b.city}
                            <c:if test="${not empty b.region}">, ${b.region}</c:if>
                            <c:if test="${not empty b.postalCode}">, ${b.postalCode}</c:if>
                            <c:if test="${not empty b.country}">, ${b.country}</c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:otherwise>
            <p>Veuillez vous connecter pour voir les immeubles.</p>
        </c:otherwise>

    </c:choose>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/buildings.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
