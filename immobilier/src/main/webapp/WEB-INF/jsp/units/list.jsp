<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="units-container">
    <h2>Unites</h2>
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success">
                ${sessionScope.success}
        </div>
        <c:remove var="success" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-error">
                ${sessionScope.error}
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>


    <c:choose>
        <c:when test="${user.role eq 'OWNER'}">
            <a href="${pageContext.request.contextPath}/units/new" class="btn-add">Ajouter une unite</a>
            <table class="units-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Numero</th>
                    <th>Pieces</th>
                    <th>Loyer</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${units}">
                    <tr>
                        <td>${u.id}</td>
                        <td>${u.unitNumber}</td>
                        <td>${u.rooms}</td>
                        <td>${u.rentAmount} FCFA</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/units/edit?id=${u.id}" class="btn-edit">Modifier</a>
                            <a href="${pageContext.request.contextPath}/units/delete?id=${u.id}" class="btn-delete"
                               onclick="return confirm('Etes-vous sur de vouloir supprimer cette unite ?');">Supprimer</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:when test="${user.role eq 'TENANT'}">
            <form method="get" action="${pageContext.request.contextPath}/units" class="filter-form">
                <input type="text" name="city" placeholder="Filtre ville"/>
                <input type="number" name="rooms" placeholder="Nb pieces"/>
                <input type="number" name="maxRent" placeholder="Loyer max"/>
                <button type="submit" class="btn-filter">Filtrer</button>
            </form>

            <table class="units-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Numero</th>
                    <th>Pieces</th>
                    <th>Loyer</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${units}">
                    <tr>
                        <td>${u.id}</td>
                        <td>${u.unitNumber}</td>
                        <td>${u.rooms}</td>
                        <td>${u.rentAmount} FCFA</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/applications/submit" style="display:inline;">
                                <input type="hidden" name="unitId" value="${u.id}"/>
                                <button type="submit" class="btn-request">Demander cette unite</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>

        <c:otherwise>
            <p>Veuillez vous connecter pour voir les unités.</p>
        </c:otherwise>
    </c:choose>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tables.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
