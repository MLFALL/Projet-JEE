<div class="register-container">
    <h2>Inscription</h2>
    <form method="post" action="${pageContext.request.contextPath}/register" class="register-form">

        <div class="form-group">
            <label for="fullName">Nom complet</label>
            <input id="fullName" type="text" name="fullName" required placeholder="Entrez votre nom complet">
        </div>

        <div class="form-group">
            <label for="email">Email</label>
            <input id="email" type="email" name="email" required placeholder="Entrez votre email">
        </div>

        <div class="form-group">
            <label for="password">Mot de passe</label>
            <input id="password" type="password" name="password" required placeholder="Entrez votre mot de passe">
        </div>

        <div class="form-group">
            <label for="phone">Telephone</label>
            <input id="phone" type="text" name="phone" placeholder="Votre numero de telephone">
        </div>

        <div class="form-group">
            <label for="role">Role</label>
            <select id="role" name="role" required>
                <option value="">-- Selectionnez un role --</option>
                <option value="TENANT">Locataire</option>
                <option value="OWNER">Proprietaire</option>
            </select>
        </div>

        <button type="submit" class="btn-register">S'inscrire</button>
    </form>


    <c:if test="${not empty error}">
        <p class="error-message">${error}</p>
    </c:if>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/register.css"/>


