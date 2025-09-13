<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<h2>Tableau de Bord Administrateur</h2>

<div class="dashboard-cards">
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/users">Utilisateurs</a></div>
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/buildings">Immeubles</a></div>
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/units">Unites</a></div>
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/applications">Demandes</a></div>
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/leases">Baux</a></div>
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/payments">Paiements</a></div>
    <div class="card"><a href="${pageContext.request.contextPath}/app/admin/reports">Rapports</a></div>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css" />

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
