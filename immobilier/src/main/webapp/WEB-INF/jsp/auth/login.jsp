<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="login-container">
    <h2>Connexion</h2>
    <form method="post" action="${pageContext.request.contextPath}/login" class="login-form">
        <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" name="email" required placeholder="Entrez votre email">
        </div>
        <div class="form-group">
            <label for="password">Mot de passe</label>
            <input id="password" type="password" name="password" required placeholder="Entrez votre mot de passe">
        </div>
        <button type="submit" class="btn-login">Se connecter</button>
    </form>

    <c:if test="${not empty error}">
        <p class="error-message">${error}</p>
    </c:if>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css"/>

<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
