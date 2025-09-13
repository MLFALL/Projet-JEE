<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="site-header">
    <div class="container">
        <h1 class="logo">Gestion Immobilier</h1>
        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/">Accueil</a>

            <c:if test="${empty user}">
                <a href="${pageContext.request.contextPath}/login">Connexion</a>
                <a href="${pageContext.request.contextPath}/register">Inscription</a>
            </c:if>

            <c:if test="${not empty user}">
                <span class="user-greeting">Bonjour, ${user.fullName} 👋</span>

                <c:choose>
                    <c:when test="${user.role eq 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/app/admin/dashboard">Dashboard Admin</a>
                        <a href="${pageContext.request.contextPath}/app/admin/users">Utilisateurs</a>
                        <a href="${pageContext.request.contextPath}/app/admin/buildings">Immeubles</a>
                        <a href="${pageContext.request.contextPath}/app/admin/units">Unites</a>
                        <a href="${pageContext.request.contextPath}/app/admin/leases">Baux</a>
                        <a href="${pageContext.request.contextPath}/app/admin/payments">Paiements</a>
                        <a href="${pageContext.request.contextPath}/app/admin/applications">Demandes</a>
                        <a href="${pageContext.request.contextPath}/app/admin/reports">Rapports</a>
                    </c:when>
                    <c:when test="${user.role eq 'OWNER'}">
                        <a href="${pageContext.request.contextPath}/buildings">Mes immeubles</a>
                        <a href="${pageContext.request.contextPath}/units">Mes unites</a>
                        <a href="${pageContext.request.contextPath}/leases">Mes locations</a>
                        <a href="${pageContext.request.contextPath}/applications">Demandes</a>
                    </c:when>
                    <c:when test="${user.role eq 'TENANT'}">
                        <a href="${pageContext.request.contextPath}/units">Offres</a>
                        <a href="${pageContext.request.contextPath}/applications">Mes demandes</a>
                        <a href="${pageContext.request.contextPath}/payments">Mes paiements</a>
                        <a href="${pageContext.request.contextPath}/leases">Mes baux</a>
                    </c:when>
                </c:choose>

                <a class="logout-btn" href="${pageContext.request.contextPath}/logout">Deconnexion</a>
            </c:if>
        </nav>
    </div>
</header>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css" />
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
