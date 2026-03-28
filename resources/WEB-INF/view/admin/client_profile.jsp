<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Администратор · Карточка клиента</title>
    <link rel="stylesheet" href="/hotelsystem/css/admin/client_profile.css">
    <script src="/hotelsystem/js/admin/client_profile.js" defer></script>
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
                    <a href="#" class="nav-link">Статистика</a>
                </div>
            </div>
            <form action="/hotelsystem/admin/logout" method="get" class="logout-form">
                <button type="submit" class="nav-link logout-link">Выйти из аккаунта</button>
            </form>
        </div>

        <!-- Заголовок страницы -->
        <div class="page-header">
            <div class="page-title">Карточка клиента</div>
            <a onclick="history.back()" class="back-link">← Назад к списку</a>
        </div>

        <div class="profile-container">
            <!-- Информация о клиенте  -->
            <div class="info-section">
                <div class="section-label">Информация о клиенте</div>
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Имя</div>
                        <div class="info-value">${client.name}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Номер телефона</div>
                        <div class="info-value">${client.phone}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Электронная почта</div>
                        <div class="info-value">${client.email}</div>
                    </div>
                </div>
            </div>

            <!-- Статус и блокировка -->
            <div class="status-section">
                <div class="status-badge">Статус: ${client.blocked == 'true' ? 'заблокирован' : 'активен'}</div>
                <button type="button" class="block-button" id="blockUserBtn" data-client-id="${client.id}">${client.blocked == 'true' ? 'Разблокировать' : 'Заблокировать'} пользователя</button>
            </div>

            <!-- История бронирований -->
            <div class="history-section">
                <div class="section-label">История бронирований</div>
                <table class="bookings-table">
                    <thead>
                        <tr>
                            <th>Комната</th>
                            <th>Даты</th>
                            <th>Кол-во гостей</th>
                            <th>Цена</th>
                            <th>Статус</th>
                            <th>Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                        for(BookingDto booking : (List<BookingDto>) request.getAttribute("bookings")){
                            pageContext.setAttribute("booking", booking);
                        %>
                        <tr>
                            <td>${booking.room.building.name} (205)</td>
                            <td>${booking.checkInDate} - ${booking.checkOutDate}</td>
                            <td><%=booking.getGuests().size()%></td>
                            <td>${booking.totalPrice} ₽</td>
                            <td><span class="booking-status ${booking.status == 'ACTIVE' ? 'confirmed' : booking.status == 'COMPLETED' ? 'completed' : 'cancelled'}">${booking.status == 'ACTIVE' ? 'Активно' : booking.status == 'COMPLETED' ? 'Завершено' : 'Отменено'}</span></td>
                            <td><a href="/hotelsystem/admin/booking/details/${booking.id}" class="details-link">Подробнее</a></td>
                        </tr>
                        <%}%>
                    </tbody>
                </table>
            </div>
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
            HavenStay — система управления клиентами
        </div>
    </div>
</body>
</html>