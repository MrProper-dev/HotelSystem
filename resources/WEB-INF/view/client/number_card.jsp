<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.RoomDto"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отель · Карточка номера</title>
    <link rel="stylesheet" href="/hotelsystem/css/client/number_card.css">
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

        <div class="room-detail-container">
            <div class="page-header">
                <div class="page-title">Номер: ${room.number}</div>
                <a onclick="history.back()" class="back-link">← Назад к списку</a>
            </div>

            <!-- Детальная сетка: фото + параметры -->
            <div class="detail-grid">
                <div class="main-image-placeholder">
                    <img src="${room.picture}" alt="Фото номера ${room.number}">
                </div>
                <div class="room-info">
                    <div class="room-general">Уютный номер для спокойного отдыха</div>
                    <div class="attributes-list">
                        <%
                        RoomDto room = (RoomDto) request.getAttribute("room");
                        Integer places = room.getSleepingPlaces();
                        %>
                        <span class="attr-item">🛌 <%=places == 1 ? places+" спальное место" : places <= 4 ? places+" спальных места" : places+" спальных мест"%></span>
                        <span class="attr-item">🏢 ${room.floor} этаж</span>
                        <span class="attr-item">🏛️ ${room.building.name}</span>
                    </div>
                    <div class="price-large">${room.price} <span class="price-per-day">/ сутки</span></div>
                </div>
            </div>

            <div class="description-block">
                <div>
                    <strong>О номере:</strong> ${room.description}
                </div>
            </div>

            <!-- КНОПКА БРОНИРОВАНИЯ -->
            <div class="booking-action">
                <a href="/hotelsystem/book/${room.id}" class="book-button">Забронировать номер</a>
            </div>
        </div>

        <div class="footer-placeholder">
            🌿 HavenStay — тишина, комфорт и забота о каждом госте
        </div>
    </div>
</body>
</html>