<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="profile-form-container">
    <h2>Mon Profil</h2>
    <form method="post" action="${pageContext.request.contextPath}/profile" class="profile-form">
        <input type="hidden" name="id" value="${user.id}"/>

        <label>Nom complet:
            <input type="text" name="fullName" value="${user.fullName}" required>
        </label>

        <label>Email:
            <input type="email" value="${user.email}" readonly>
        </label>

        <label>Mot de passe (nouveau):
            <input type="password" name="password">
        </label>

        <button type="submit" class="btn-save">Mettre a jour</button>
    </form>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/forms.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
