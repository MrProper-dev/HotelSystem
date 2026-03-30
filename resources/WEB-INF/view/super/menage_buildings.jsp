<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Суперпользователь · Управление корпусами</title>
    <link rel="stylesheet" href="/hotelsystem/css/super/menage_buildings.css">
    <script src="/hotelsystem/js/super/menage_buildings.js" defer></script>
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация для суперпользователя -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="/hotelsystem/super/rooms" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">super</span></span>
                </a>
                <div class="nav-links">
                    <a href="/hotelsystem/super/rooms" class="nav-link">Комнаты</a>
                    <a href="#" class="nav-link active">Корпусы</a>
                    <a href="/hotelsystem/super/admins" class="nav-link">Администраторы</a>
                </div>
            </div>
        </div>

        <div class="page-header">
            <div class="page-title">Управление корпусами</div>
        </div>

        <!-- Форма добавления нового корпуса -->
        <div class="add-section">
            <div class="add-header">Добавление нового корпуса</div>
            <form class="add-form" id="addBuildingForm" method="POST" action="">
                <div class="form-field">
                    <div class="field-label">Название корпуса</div>
                    <div class="field-input">
                        <input type="text" name="name" placeholder="Корпус А" required>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">Адрес</div>
                    <div class="field-input">
                        <input type="text" name="address" placeholder="ул. Ленина, д. 10" required>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">Количество этажей</div>
                    <div class="field-input">
                        <input type="number" name="floors" placeholder="5" min="1" max="50" required>
                    </div>
                </div>
                <button type="submit" class="add-button">Добавить корпус</button>
            </form>
        </div>

        <div class="list-header">
            <div class="list-header-left">Существующие корпуса</div>
            <div class="list-header-right" id="buildingsCount">Всего корпусов: ${buildingsCount}</div>
        </div>

        <!-- Таблица корпусов -->
        <table class="buildings-table" id="buildingsTable">
            <thead>
                <tr>
                    <th>Название</th>
                    <th>Адрес</th>
                    <th>Количество этажей</th>
                    <th class="actions-cell">Действия</th>
                 </thead>
            <tbody>
                <%
                List<BuildingDto> buildings = (List<BuildingDto>) request.getAttribute("buildings");
                for(BuildingDto building : buildings){
                    pageContext.setAttribute("building", building);
                %>
                <tr data-building-id="${building.id}">
                    <td class="name-cell">${building.name}</td>
                    <td class="address-cell">${building.address}</td>
                    <td class="floors-cell">${building.floors}</td>
                    <td class="actions-cell">
                        <button class="action-link edit" data-action="edit">Редактировать</button>
                        <button class="action-link delete" data-action="delete">Удалить</button>
                    </td>
                </tr>
                <%}%>
            </tbody>
        </table>

        <!-- Пагинация -->
        <div class="pagination">
            <%
            Integer currentPage = (Integer)request.getAttribute("currentPage");
            String query = request.getQueryString();
            String params = query;
            if (query != null) {
                int pagePos = query.indexOf("page=");
                if (pagePos != -1) {
                    if(pagePos != 0){
                        pagePos--;
                    } 
                    params = query.substring(0, pagePos);
                }
            }
            %>
            <%for(int i = 0; i<currentPage; i++){%>
            <a href="<%=(params!=null && !params.isEmpty()) ? "?"+params+"&" : "?"%>page=<%=i%>" class="page-item"><%=i+1%></a>
            <%}%>
            <a href="<%=(params!=null && !params.isEmpty()) ? "?"+params+"&" : "?"%>page=${currentPage}" class="page-item active">${currentPage+1}</a>
            <%for(int i = currentPage+1; i<(Integer)request.getAttribute("pageCount"); i++){%>
            <a href="<%=(params!=null && !params.isEmpty()) ? "?"+params+"&" : "?"%>page=<%=i%>" class="page-item"><%=i+1%></a>
            <%}%>
        </div>

        <!-- модальное окно (confirm) -->
        <dialog id="confirmModal" class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
                    <p id="confirmModalText">Подтвердите действие.</p>
                </div>
                <div class="modal-footer confirm-buttons">
                    <button id="confirmModalCancel" class="button-primary">Назад</button>
                    <button id="confirmModalOk" class="button-primary">Верно</button>
                </div>
            </div>
        </dialog>

        <!-- модальное окно (info) -->
        <dialog id="infoModal" class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
                    <p id="infoModalText">Сообщение по умолчанию.</p>
                </div>
                <div class="modal-footer">
                    <button id="infoModalOk" class="button-primary">ОК</button>
                </div>
            </div>
        </dialog>

        <div class="footer-placeholder">
            HavenStay — система управления корпусами
        </div>
    </div>
</body>
</html>