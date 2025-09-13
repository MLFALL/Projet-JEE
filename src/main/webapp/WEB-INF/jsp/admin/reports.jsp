<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jsp/common/header.jsp" %>
<c:choose>
    <c:when test="${user.role eq 'ADMIN'}">
        <h2>Rapports & Statistiques</h2>

        <div class="stats-cards">
            <div class="stat-card">
                <span class="stat-title">Utilisateurs</span>
                <span class="stat-value">${stats.totalUsers}</span>
            </div>
            <div class="stat-card">
                <span class="stat-title">Immeubles</span>
                <span class="stat-value">${stats.totalBuildings}</span>
            </div>
            <div class="stat-card">
                <span class="stat-title">Unites</span>
                <span class="stat-value">${stats.totalUnits}</span>
            </div>
            <div class="stat-card">
                <span class="stat-title">Baux</span>
                <span class="stat-value">${stats.totalLeases}</span>
            </div>
            <div class="stat-card">
                <span class="stat-title">Paiements</span>
                <span class="stat-value">${stats.totalPayments}</span>
            </div>
        </div>

        <div class="charts-container">
            <div class="chart-box">
                <h3>Repartition des Utilisateurs</h3>
                <canvas id="usersChart"></canvas>
            </div>
            <div class="chart-box">
                <h3>Paiements par Mois</h3>
                <canvas id="paymentsChart"></canvas>
            </div>
            <div class="chart-box">
                <h3>Baux actifs vs expires</h3>
                <canvas id="leasesChart"></canvas>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <p class="unauthorized">Vous n’avez pas l’autorisation d’acceder à cette page.</p>
    </c:otherwise>
</c:choose>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/stats.css" />
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>

    const usersData = [
        <c:forEach var="count" items="${stats.userCounts}" varStatus="loop">
        ${count}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];
    const usersLabels = ["Admins", "Proprietaires", "Locataires"];
    const usersColors = ['#FFD700', '#0047AB', '#0066CC'];

    new Chart(document.getElementById('usersChart'), {
        type: 'doughnut',
        data: {
            labels: usersLabels,
            datasets: [{
                data: usersData,
                backgroundColor: usersColors
            }]
        }
    });

    const paymentsLabels = [
        <c:forEach var="m" items="${stats.months}" varStatus="loop">
        "${m}"<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];
    const paymentsData = [
        <c:forEach var="p" items="${stats.paymentsByMonth}" varStatus="loop">
        ${p}<c:if test="${!loop.last}">,</c:if>
        </c:forEach>
    ];

    new Chart(document.getElementById('paymentsChart'), {
        type: 'line',
        data: {
            labels: paymentsLabels,
            datasets: [{
                label: 'Montant paye',
                data: paymentsData,
                fill: false,
                borderColor: '#FFD700',
                tension: 0.3
            }]
        }
    });

    const leasesLabels = ['Actifs', 'Expires'];
    const leasesData = [${stats.activeLeases}, ${stats.expiredLeases}];
    const leasesColors = ['#0047AB', '#FFD700'];

    new Chart(document.getElementById('leasesChart'), {
        type: 'bar',
        data: {
            labels: leasesLabels,
            datasets: [{
                label: 'Nombre de baux',
                data: leasesData,
                backgroundColor: leasesColors
            }]
        }
    });
</script>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
