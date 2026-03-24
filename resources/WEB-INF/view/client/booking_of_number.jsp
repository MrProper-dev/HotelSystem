<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.RoomDto"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отель · Создание брони</title>
    <link rel="stylesheet" href="/hotelsystem/css/client/booking_of_number.css">
    <script src="/hotelsystem/js/client/booking_of_number.js" defer></script>
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
                    <a href="#" class="nav-link">Мои брони</a>
                    <a href="#" class="nav-link">Профиль</a>
                </div>
            </div>
        </div>

        <!-- Заголовок страницы -->
        <div class="page-header">
            <div class="page-title">📋 Создание брони</div>
            <a onclick="history.back()" class="back-link">← Назад к номеру</a>
        </div>

        <!-- Основной контейнер -->
        <div class="booking-container">
            <form id="bookingForm" method="get" action="some">
                <!-- 1. Выбранная комната -->
                <div class="selected-room-section">
                    <div class="section-label">Выбранная комната</div>
                    <div class="room-card-fixed">
                        <div class="room-photo">
                            <img src="${room.picture}" alt="Номер: ${room.number}">
                        </div>
                        <div class="room-info-fixed">
                            <div class="room-type">Номер: ${room.number}</div>
                            <div class="room-features">
                                <%
                                RoomDto room = (RoomDto) request.getAttribute("room");
                                Integer places = room.getSleepingPlaces();
                                %>
                                <span class="feature">🛌 <%=places == 1 ? places+" спальное место" : places <= 4 ? places+" спальных места" : places+" спальных мест"%></span>
                                <span class="feature">🏢 ${room.floor} этаж</span>
                                <span class="feature">🏛️ ${room.building.name}</span>
                            </div>
                            <div class="room-price">${room.price} <span class="price-per-day">/ сутки</span></div>
                        </div>
                    </div>
                    <input type="hidden" name="room_id" value="${room.id}">
                    <input type="hidden" name="price_per_night" value="4200" id="pricePerNight">
                </div>

                <!-- 2. Даты (форма с полями ввода) -->
                <div class="section-label">Даты проживания</div>
                <div class="dates-section">
                    <div class="dates-grid">
                        <div class="date-field">
                            <div class="date-label">Дата заезда</div>
                            <div class="date-input">
                                <input type="date" name="checkin" id="checkin" value="2026-06-15" required>
                            </div>
                        </div>
                        <div class="date-field">
                            <div class="date-label">Дата выезда</div>
                            <div class="date-input">
                                <input type="date" name="checkout" id="checkout" value="2026-06-20" required>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 3. Данные гостей (динамическое добавление) -->
                <div class="section-label">Данные гостей</div>
                <div class="guests-section" id="guestsSection">
                    <div id="guestsContainer">
                        <!-- Гость 1 (обязательный) -->
                        <div class="guest-card" data-guest-id="1">
                            <div class="guest-header">
                                <div class="guest-title">Гость 1</div>
                            </div>
                            <div class="guest-fields">
                                <div class="field-group">
                                    <div class="field-label">Фамилия</div>
                                    <input type="text" class="field-input" name="guests[0][surname]" placeholder="Иванов" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Имя</div>
                                    <input type="text" class="field-input" name="guests[0][name]" placeholder="Иван" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Отчество</div>
                                    <input type="text" class="field-input" name="guests[0][patronymic]" placeholder="Иванович">
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Дата рождения</div>
                                    <input type="date" class="field-input" name="guests[0][birthdate]" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Серия пасп. / свид. о рожд.</div>
                                    <input type="text" class="field-input" name="guests[0][passport_series]" placeholder="1234 / I-АЮ" required>
                                </div>
                                <div class="field-group">
                                    <div class="field-label">Номер пасп. / свид. о рожд.</div>
                                    <input type="text" class="field-input" name="guests[0][passport_number]" placeholder="567890" required>
                                </div>
                            </div>
                        </div>
                    </div>
                    <button type="button" class="add-guest-button" id="addGuestBtn">+ Добавить гостя</button>
                </div>

                <!-- Итоговая стоимость -->
                <div class="summary-block" id="summaryBlock">
                    <div class="summary-item" id="roomSummary">Комната: Стандарт "Лайт"</div>
                    <div class="summary-item" id="nightsSummary">0 ночей</div>
                    <div class="total-price" id="totalPrice">0</div>
                </div>

                <!-- Кнопка подтверждения -->
                <div class="confirm-button-container">
                    <button type="submit" class="button-primary">Подтвердить бронь</button>
                </div>
            </form>
        </div>

        <div class="footer-placeholder">
            🌿 HavenStay — тишина, комфорт и забота о каждом госте
        </div>
    </div>

</body>
</html>