<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Администратор · Карточка номера</title>
    <link rel="stylesheet" href="/hotelsystem/css/admin/number_card.css">
    <script src="/hotelsystem/js/admin/number_card.js" defer></script>
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="/hotelsystem/admin/rooms" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">admin</span></span>
                </a>
                <div class="nav-links">
                    <a href="/hotelsystem/admin/rooms" class="nav-link">Номерной фонд</a>
                    <a href="/hotelsystem/admin/clients" class="nav-link">Клиенты</a>
                </div>
            </div>
            <form action="/hotelsystem/admin/logout" method="get" class="logout-form">
                <button type="submit" class="nav-link logout-link">Выйти из аккаунта</button>
            </form>
        </div>

        <div class="page-header">
            <div class="page-title">Карточка номера</div>
            <a onclick="history.back()" class="back-link">← Назад к списку</a>
        </div>

        <div class="room-detail-container">
            <form id="roomForm" method="POST" action="" enctype="multipart/form-data" data-room-id="${room.id}">
                <!-- Скрытое поле для загрузки фото -->
                <input type="file" id="fileInput" name="picture" accept="image/*">
                
                <!-- Статусная строка -->
                <div class="status-bar">
                    <div class="room-status ${room.status == 'BUSY' ? 'occupied' : 'available'}">Текущий статус: ${room.status == 'BUSY' ? 'занят' : 'свободен'}</div>
                </div>

                <!-- Информация о номере -->
                <div class="section-label">Информация о номере</div>
                <div class="info-grid">
                    <div class="photo-placeholder" id="photoPlaceholder">
                        <img src="/hotelsystem/images/rooms/${room.picture}" alt="Номер ${room.number}" id="roomImage">
                        <div class="upload-overlay">Нажмите, чтобы загрузить фото</div>
                    </div>
                    <div class="room-info">
                        <div class="info-row">
                            <span class="info-label">Номер комнаты</span>
                            <div class="info-value editable">
                                <input type="number" name="number" value="${room.number}" required>
                            </div>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Количество мест</span>
                            <div class="info-value editable">
                                <input type="number" name="guests" value="${room.sleepingPlaces}" step="1" required>
                            </div>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Этаж</span>
                            <div class="info-value editable">
                                <select name="floor">
                                    <%
                                    List<BuildingDto> buildingDtos = (List<BuildingDto>) request.getAttribute("buildings");
                                    RoomDto roomDto = (RoomDto) request.getAttribute("room");
                                    Integer maxFloors = roomDto.getBuilding().getFloors();
                                    Integer currentFloor = roomDto.getFloor();
                                    for(int i = 1; i < currentFloor; i++){
                                    %>
                                    <option value="<%=i%>"><%=i%> этаж</option>
                                    <%}%>
                                    <option value="${room.floor}" selected>${room.floor} этаж</option>
                                    <%for(int i = currentFloor+1; i <= maxFloors; i++){%>
                                    <option value="<%=i%>"><%=i%> этаж</option>
                                    <%}%>
                                </select>
                            </div>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Корпус</span>
                            <div class="info-value editable">
                                <select name="building">
                                    <option value="${room.building.id}" selected>${room.building.name}</option>
                                    <%
                                    for(BuildingDto buildingDto : buildingDtos){
                                        if(buildingDto.getId() != roomDto.getBuilding().getId()){
                                    %>
                                    <option value="<%=buildingDto.getId()%>" data-building-floors="<%=buildingDto.getFloors()%>"><%=buildingDto.getName()%></option>
                                    <%}}%>
                                </select>
                            </div>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Цена за сутки</span>
                            <div class="info-value editable">
                                <input type="number" name="price" value="${room.price}" required>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Описание -->
                <div class="description-row">
                    <div class="section-label">Описание номера</div>
                    <div class="description-field">
                        <textarea name="description" rows="4" placeholder="Введите описание номера...">${room.description}</textarea>
                    </div>
                </div>

                <!-- История бронирований гостей в этом номере -->
                <div class="history-section">
                    <div class="section-label">История бронирований номера</div>
                    <table class="history-table">
                        <thead>
                            <tr>
                                <th>Клиент</th>
                                <th>Даты заезда/выезда</th>
                                <th>Статус</th>
                                <th style="text-align: right;">Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                            List<BookingDto> bookingDtos = (List<BookingDto>) request.getAttribute("bookings");
                            for(BookingDto bookingDto : bookingDtos){
                            %>
                            <tr>
                                <td><%=bookingDto.getClient().getName()%></td>
                                <td><%=bookingDto.getCheckInDate()%> - <%=bookingDto.getCheckOutDate()%></td>
                                <td><%=bookingDto.getStatus() == BookingStatus.ACTIVE ? "Активно" : bookingDto.getStatus() == BookingStatus.COMPLETED ? "Завершено" : "Отменено"%></td>
                                <td class="actions-cell">
                                    <a href="/hotelsystem/admin/client/details/<%=bookingDto.getClient().getId()%>" class="guest-link">Профиль</a>
                                    <a href="/hotelsystem/admin/booking/details/<%=bookingDto.getId()%>" class="guest-link">Подробно</a>
                                </td>
                            </tr>
                            <%}%>
                        </tbody>
                    </table>
                </div>

                <!-- Кнопка сохранения изменений -->
                <div class="action-buttons">
                    <button type="submit" class="button primary">Сохранить изменения</button>
                </div>
            </form>
        </div>

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
            HavenStay — система управления номерным фондом
        </div>
    </div>
</body>
</html>