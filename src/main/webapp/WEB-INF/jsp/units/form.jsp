<%@ include file="/WEB-INF/jsp/common/header.jsp" %>

<div class="unit-form-container">
    <c:if test="${user.role eq 'OWNER'}">
        <h2>${unit.id == null ? "Nouvelle Unite" : "Modifier Unite"}</h2>
        <form method="post" action="${pageContext.request.contextPath}/units/save" class="unit-form">
            <input type="hidden" name="id" value="${unit.id}"/>

            <label>Numero unite:
                <input type="text" name="unitNumber" value="${unit.unitNumber}" required>
            </label>

            <label>Nb pieces:
                <input type="number" name="rooms" value="${unit.rooms}" required>
            </label>

            <label>Loyer (FCFA):
                <input type="number" name="rentAmount" value="${unit.rentAmount}" required>
            </label>

            <label>ID Immeuble:
                <c:if test="${not empty availableBuildings}">
                    <select name="buildingId" required>
                        <option value="">-- Selectionner un immeuble disponible --</option>
                        <c:forEach var="building" items="${availableBuildings}">
                            <option value="${building.id}"
                                ${unit.building != null && unit.building.id eq building.id ? "selected" : ""}>
                                    ${building.name}
                            </option>
                        </c:forEach>
                    </select>
                </c:if>
                <c:if test="${empty availableBuildings}">
                    <p style="color:red;">Aucun immeuble disponible pour creer une unité.</p>
                </c:if>
            </label>

            <button type="submit" class="btn-save">Enregistrer</button>
        </form>
    </c:if>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/forms.css"/>
<%@ include file="/WEB-INF/jsp/common/footer.jsp" %>
