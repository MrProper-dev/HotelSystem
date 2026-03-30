<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отель · Детали бронирования</title>
    <link rel="stylesheet" href="/hotelsystem/css/client/booking_details.css">
    <script src="/hotelsystem/js/client/booking_details.js" defer></script>
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="/hotelsystem/rooms" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">hotel</span></span>
                </a>
                <div class="nav-links">
                    <a href="/hotelsystem/rooms" class="nav-link">Комнаты</a>
                    <a href="/hotelsystem/booking/history" class="nav-link">Мои брони</a>
                    <a href="/hotelsystem/profile" class="nav-link">Профиль</a>
                </div>
            </div>
        </div>

        <div class="page-header">
            <div class="page-title">📋 Детали бронирования</div>
            <a onclick="history.back()" class="back-link">← Назад к списку</a>
        </div>

        <div class="detail-container">
            <!-- Строка статуса и номера брони -->
            <div class="status-bar">
                <div class="status-label ${booking.status == 'ACTIVE' ? 'confirmed' : (booking.status == 'CANCELED' ? 'cancelled' : 'completed')}">
                    ${booking.status == 'ACTIVE' ? 'Активно' : (booking.status == 'CANCELED' ? 'Отменено' : 'Завершено')}
                </div>
                <div class="booking-number">Бронь № ${booking.id}</div>
            </div>

            <!-- 1. Выбранная комната -->
            <div class="selected-room-section">
                <div class="section-label">Выбранная комната</div>
                <div class="room-card-fixed">
                    <div class="room-photo">
                        <img src="/hotelsystem/images/rooms/${booking.room.picture}" alt="Комната ${booking.room.number}">
                    </div>
                    <div class="room-info-fixed">
                        <div class="room-type">Номер ${booking.room.number}</div>
                        <div class="room-features">
                            <span class="feature">🛌 ${booking.room.sleepingPlaces} спальных мест</span>
                            <span class="feature">🏢 ${booking.room.floor} этаж</span>
                            <span class="feature">🏛️ корпус ${booking.room.building.name}</span>
                        </div>
                        <div class="room-price">${booking.room.price} <span class="price-per-day">/ сутки</span></div>
                    </div>
                </div>
            </div>

            <!-- 2. Даты проживания -->
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

            <!-- 3. Данные гостей -->
            <div class="section-label">Данные гостей</div>
            <div class="guests-section">
                <%
                BookingDto booking = (BookingDto) request.getAttribute("booking");
                List<GuestDto> guests = booking.getGuests();
                for(int i = 0; i < guests.size(); i++) {
                    GuestDto guest = guests.get(i);
                    String fullName = guest.getFullName();
                    String lastName = "";
                    String firstName = "";
                    String patronymic = "";
                    
                    if (fullName != null && !fullName.isEmpty()) {
                        String[] nameParts = fullName.split(" ");
                        if (nameParts.length >= 1) lastName = nameParts[0];
                        if (nameParts.length >= 2) firstName = nameParts[1];
                        if (nameParts.length >= 3) patronymic = nameParts[2];
                    }
                    
                    String seriesAndNumber = guest.getSeriesAndNumber();
                    String series = "";
                    String number = "";
                    
                    if (seriesAndNumber != null && !seriesAndNumber.isEmpty()) {
                        String[] snParts = seriesAndNumber.split(" ");
                        if (snParts.length >= 1) series = snParts[0];
                        if (snParts.length >= 2) number = snParts[1];
                    }
                %>
                    <div class="guest-card">
                        <div class="guest-title">Гость <%= i+1 %></div>
                        <div class="guest-fields">
                            <div class="field-group">
                                <div class="field-label">Фамилия</div>
                                <div class="field-value"><%= lastName %></div>
                            </div>
                            <div class="field-group">
                                <div class="field-label">Имя</div>
                                <div class="field-value"><%= firstName %></div>
                            </div>
                            <div class="field-group">
                                <div class="field-label">Отчество</div>
                                <div class="field-value"><%= patronymic %></div>
                            </div>
                            <div class="field-group">
                                <div class="field-label">Дата рождения</div>
                                <div class="field-value"><%= guest.getBirthDate() %></div>
                            </div>
                            <div class="field-group">
                                <div class="field-label">Серия паспорта / свид. о рождении</div>
                                <div class="field-value"><%= series %></div>
                            </div>
                            <div class="field-group">
                                <div class="field-label">Номер паспорта / свид. о рождении</div>
                                <div class="field-value"><%= number %></div>
                            </div>
                        </div>
                    </div>
                <%
                }
                %>
            </div>

            <div class="summary-block">
                <%
                    LocalDate checkIn = booking.getCheckInDate();
                    LocalDate checkOut = booking.getCheckOutDate();
                    long nights = 0;
                    if (checkIn != null && checkOut != null) {
                        nights = ChronoUnit.DAYS.between(checkIn, checkOut);
                    }
                    int guestCount = guests != null ? guests.size() : 0;
                %>
                <div class="summary-item">📅 <%= nights %> ночей</div>
                <div class="summary-item">👥 <%= guestCount %> гостей</div>
                <div class="total-price"><%= booking.getTotalPrice() %></div>
            </div>

            <!-- Кнопки действий -->
            <div class="action-buttons">
                <form method="POST" action="" class="cancel-form">
                    <button type="submit" class="button cancel" id="cancelBookingBtn" 
                            ${booking.status != 'ACTIVE' ? 'disabled' : ''}
                            style="${booking.status != 'ACTIVE' ? 'opacity: 0.5; cursor: not-allowed;' : ''}"
                            data-booking-id="${booking.id}">
                        🗑️ Отменить бронь
                    </button>
                </form>
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
                    <button id="confirmModalOk" class="button-primary">Подтвердить</button>
                </div>
            </div>
        </dialog>

        <div class="footer-placeholder">
            🌿 HavenStay — тишина, комфорт и забота о каждом госте
        </div>
    </div>
</body>
</html>