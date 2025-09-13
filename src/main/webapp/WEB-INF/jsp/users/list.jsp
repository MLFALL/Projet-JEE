<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<c:choose>
    <c:when test="${user.role eq 'ADMIN'}">
        <div class="table-container">
            <h2>Liste des Utilisateurs</h2>
            <table class="styled-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Email</th>
                    <th>Role</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>${u.id}</td>
                        <td>${u.fullName}</td>
                        <td>${u.email}</td>
                        <td>
                            <span class="role-badge
                                ${u.role eq 'ADMIN' ? 'role-admin' : (u.role eq 'OWNER' ? 'role-owner' : 'role-tenant')}">
                                    ${u.role}
                            </span>
                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:when>

    <c:otherwise>
        <p>Vous n'êtes pas autorisé à voir cette page.</p>
    </c:otherwise>
</c:choose>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tables.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
