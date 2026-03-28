<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Администратор · Детали бронирования</title>
    <link rel="stylesheet" href="/hotelsystem/css/admin/booking_details.css">
    <script src="/hotelsystem/js/admin/booking_details.js" defer></script>
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация для администратора -->
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
            <div class="page-title">Детали бронирования</div>
            <a onclick="history.back()" class="back-link">← Назад к списку</a>
        </div>

        <div class="detail-container">
            <form id="bookingForm" method="POST" action="" data-booking-id="${booking.id}">
                <!-- Строка статуса и номера брони -->
                <div class="status-bar">
                    <div class="status-label" id="bookingStatus">${booking.status == 'ACTIVE' ? 'Активно' : booking.status == 'COMPLETED' ? 'Завершено' : 'Отменено'}</div>
                    <div class="booking-number">Бронь № ${booking.id}</div>
                </div>

                <!-- Выбранная комната -->
                <div class="selected-room-section">
                    <div class="section-label">Выбранная комната</div>
                    <div class="room-card-fixed">
                        <div class="room-photo">
                            <img src="/hotelsystem/images/rooms/${booking.room.picture}" alt="Номер ${booking.room.number}">
                        </div>
                        <div class="room-info-fixed">
                            <div class="room-type">Номер ${booking.room.number}</div>
                            <div class="room-features">
                                <span class="feature">Cпальныt места: ${booking.room.sleepingPlaces}</span>
                                <span class="feature">Этаж: ${booking.room.floor}</span>
                                <span class="feature">${booking.room.building.name}</span>
                            </div>
                            <div class="room-price">${booking.room.price} <span class="price-per-day">/ сутки</span></div>
                        </div>
                    </div>
                </div>

                <!-- Даты  -->
                <div class="section-label">Даты проживания</div>
                <div class="dates-section">
                    <div class="dates-container">
                        <div class="date-row">
                            <span class="date-label">Заезд:</span>
                            <span class="date-value">${booking.checkInDate}</span>
                        </div>
                        <div class="date-row">
                            <span class="date-label">Выезд:</span>
                            <span class="date-value">${booking.checkOutDate}</span>
                        </div>
                    </div>
                </div>

                <!-- Данные гостей -->
                <div class="section-label">Данные гостей</div>
                <div class="guests-section" id="guestsSection">
                    <div id="guestsContainer">
                        <%
                        BookingDto bookingDto = (BookingDto) request.getAttribute("booking");
                        for(int i = 0; i < bookingDto.getGuests().size(); i++){
                            GuestDto guestDto = bookingDto.getGuests().get(i);
                            String[] lfs = guestDto.getFullName().split(" ");
                            String[] sn = guestDto.getSeriesAndNumber().split(" ");
                        %>
                        <div class="guest-card" data-guest-id="<%=i%>">
                            <div class="guest-header">
                                <div class="guest-title">Гость <%=i+1%></div>
                                <button type="button" class="remove-guest" data-guest-index="<%=i%>">Удалить</button>
                            </div>
                            <div class="guest-fields">
                                <div class="field-group">
                                    <div class="field-label">Фамилия</div>
                                    <input type="text" class="field-input" name="guests[<%=i%>][last_name]" value="<%=lfs[0]%>" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Имя</div>
                                    <input type="text" class="field-input" name="guests[<%=i%>][first_name]" value="<%=lfs[1]%>" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Отчество</div>
                                    <input type="text" class="field-input" name="guests[<%=i%>][surname]" value="<%=lfs.length == 3 ? lfs[2] : ""%>">
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Дата рождения</div>
                                    <input type="date" class="field-input" name="guests[<%=i%>][birthdate]" value="<%=guestDto.getBirthDate()%>" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Серия пасп. / свид. о рожд.</div>
                                    <input type="text" class="field-input" name="guests[0][doc_series]" value="<%=sn[0]%>" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Номер пасп. / свид. о рожд.</div>
                                    <input type="text" class="field-input" name="guests[0][doc_number]" value="<%=sn[1]%>" required>
                                </div>
                            </div>
                        </div>
                        <%}%>

                    </div>
                    <button type="button" class="add-guest-button" id="addGuestBtn">+ Добавить гостя</button>
                </div>

                <!-- Итоговая стоимость -->
                <div class="summary-block">
                    <%
                    LocalDate checkIn = bookingDto.getCheckInDate();
                    LocalDate checkOut = bookingDto.getCheckOutDate();
                    long nights = 0;
                    if (checkIn != null && checkOut != null) {
                        nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                    }
                    %>
                    <div class="summary-item">Ночей: <%=nights%></div>
                    <div class="summary-item" id="guestsCount">Гостей: <%=bookingDto.getGuests().size()%></div>
                    <div class="total-price"><%=bookingDto.getTotalPrice()%></div>
                </div>

                <!-- Кнопки действий -->
                <div class="action-buttons">
                    <button type="button" class="button cancel" id="cancelBookingBtn" ${booking.status == 'CANCELED' ? 'disabled' : ''} ${booking.status == 'CANCELED' ? 'style="opacity: 0.4; cursor: not-allowed;"' : ''}>Отменить бронь</button>
                    <button type="submit" class="button primary">Сохранить изменения</button>
                </div>
            </form>
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
            HavenStay — система управления бронированиями
        </div>
    </div>
</body>
</html>