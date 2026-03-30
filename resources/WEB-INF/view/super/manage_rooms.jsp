<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Суперпользователь · Управление комнатами</title>
    <link rel="stylesheet" href="/hotelsystem/css/super/manage_rooms.css">
    <script src="/hotelsystem/js/super/manage_rooms.js" defer></script>
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="#" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">super</span></span>
                </a>
                <div class="nav-links">
                    <a href="#" class="nav-link active">Комнаты</a>
                    <a href="/hotelsystem/super/buildings" class="nav-link">Корпусы</a>
                    <a href="/hotelsystem/super/admins" class="nav-link">Администраторы</a>
                </div>
            </div>
        </div>

        <div class="page-header">
            <div class="page-title">Управление комнатами</div>
        </div>

        <!-- Форма добавления новой комнаты -->
        <div class="add-section">
            <div class="add-header">Добавление новой комнаты</div>
            <form class="add-form" id="addRoomForm" method="POST" action="">
                <div class="form-field">
                    <div class="field-label">Номер комнаты</div>
                    <div class="field-input">
                        <input type="text" name="room_number" placeholder="101" required>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">Этаж</div>
                    <div class="field-input">
                        <select name="floor">
                            <%
                            List<BuildingDto> buildingDtos = (List<BuildingDto>) request.getAttribute("buildings");
                            Integer maxFloors = buildingDtos.get(0).getFloors();
                            for(int i = 1; i <= maxFloors; i++){
                            %>
                            <option value="<%=i%>"><%=i%> этаж</option>
                            <%}%>
                        </select>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">Корпус</div>
                    <div class="field-input">
                        <select name="building">
                            <%
                            for(BuildingDto buildingDto : buildingDtos){
                            %>
                            <option value="<%=buildingDto.getId()%>" data-building-floors="<%=buildingDto.getFloors()%>"><%=buildingDto.getName()%></option>
                            <%}%>
                        </select>
                    </div>
                </div>
                <button type="submit" class="add-button">Добавить комнату</button>
            </form>
        </div>

        <!-- Фильтры для комнат -->
        <div class="filters-section">
            <div class="filter-header">Фильтры</div>
            <form class="filter-form" id="filterForm" method="GET" action="">
                <div class="filter-grid">
                    <div class="filter-item">
                        <div class="filter-label">Корпус</div>
                        <div class="filter-field">
                            <select name="building">
                                <option value="">Все корпуса</option>
                                <%
                                for(BuildingDto building : buildingDtos){
                                %>
                                <option value="<%=building.getId()%>"><%=building.getName()%></option>
                                <%}%>
                            </select>
                        </div>
                    </div>
                    <div class="filter-item">
                        <div class="filter-label">Этаж</div>
                        <div class="filter-field">
                            <select name="floor">
                                <option value="">Все этажи</option>
                                <%
                                Integer floor = (Integer) request.getAttribute("maxFloot");
                                for(int i = 1; i <= floor; i++){
                                %>
                                <option value="<%=i%>"><%=i%></option>
                                <%}%>
                            </select>
                        </div>
                    </div>
                    <button type="submit" class="filter-button">Применить</button>
                </div>
            </form>
        </div>

        <div class="list-header">
            <div class="list-header-left">Список комнат</div>
            <div class="list-header-right" id="roomsCount">Всего комнат: ${roomsCount}</div>
        </div>

        <!-- Таблица комнат -->
        <table class="rooms-table" id="roomsTable">
            <thead>
                <tr>
                    <th>Номер комнаты</th>
                    <th>Этаж</th>
                    <th>Корпус</th>
                    <th>Действия</th>
                 </thead>
            <tbody>
                <%
                List<RoomDto> roomDtos = (List<RoomDto>) request.getAttribute("rooms");
                for(RoomDto room : roomDtos){
                    pageContext.setAttribute("room", room);
                %>
                <tr data-room-id="${room.id}">
                    <td class="room-number-cell">${room.number}</td>
                    <td class="floor-cell">${room.floor}</td>
                    <td class="building-cell">${room.building.name}</td>
                    <td>
                        <button class="action-link delete">Удалить</button>
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
            HavenStay — система управления комнатами
        </div>
    </div>
</body>
</html>