<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="form-container">
    <c:if test="${user.role eq 'OWNER' or user.role eq 'ADMIN'}">
        <h2>${building.id == null ? "Nouvel Immeuble" : "Modifier Immeuble"}</h2>

        <c:if test="${not empty error}">
            <div class="error-message" style="color:red; margin-bottom:10px;">
                    ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/buildings" class="building-form">
            <input type="hidden" name="id" value="${building.id}"/>

            <label>Nom:
                <input type="text" name="name" value="${building.name}" required>
            </label>

            <label>Rue:
                <input type="text" name="street" value="${building.street}" required>
            </label>

            <label>Ville:
                <input type="text" name="city" value="${building.city}" required>
            </label>

            <label>Région:
                <input type="text" name="region" value="${building.region}" />
            </label>

            <label>Code Postal:
                <input type="text" name="postalCode" value="${building.postalCode}" />
            </label>

            <label>Pays:
                <input type="text" name="country" value="${building.country}" />
            </label>

            <label>Description:
                <textarea name="description" rows="4">${building.description}</textarea>
            </label>

            <label>Equipements:
                <textarea name="amenities" rows="4">${building.amenities}</textarea>
            </label>

            <button type="submit" class="btn-submit">Enregistrer</button>
        </form>
    </c:if>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/form.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
