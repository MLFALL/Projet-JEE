<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<main>
    <h2>Bienvenue sur la plateforme de gestion immobiliere</h2>

    <c:choose>

        <c:when test="${empty user}">
            <p>Veuillez vous <a href="${pageContext.request.contextPath}/login">connecter</a>
                ou <a href="${pageContext.request.contextPath}/register">vous inscrire</a> pour continuer.</p>
        </c:when>


        <c:otherwise>
            <p>Bonjour ${user.fullName} 👋</p>
            <p>
                <c:choose>
                    <c:when test="${user.role eq 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/app/admin/dashboard">Aller au tableau de bord Admin</a>
                    </c:when>
                    <c:when test="${user.role eq 'OWNER'}">
                        <a href="${pageContext.request.contextPath}/buildings">Aller a mon tableau de bord Proprietaire</a>
                    </c:when>
                    <c:when test="${user.role eq 'TENANT'}">
                        <a href="${pageContext.request.contextPath}/units">Aller a mon tableau de bord Locataire</a>
                    </c:when>
                </c:choose>
            </p>
        </c:otherwise>

    </c:choose>
</main>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css"/>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
