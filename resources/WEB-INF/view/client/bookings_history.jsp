<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.List"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отель · Мои бронирования</title>
    <link rel="stylesheet" href="/hotelsystem/css/client/bookings_history.css">
    <script src="/hotelsystem/js/client/bookings_history.js" defer></script>
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
                    <a href="#" class="nav-link active">Мои брони</a>
                    <a href="/hotelsystem/profile" class="nav-link">Профиль</a>
                </div>
            </div>
        </div>

        <div class="page-header">
            <div class="page-title">📋 История бронирований</div>
        </div>

        <div class="bookings-container">
            <div class="bookings-list">
                <!-- Карточка -->
                <%
                List<BookingDto> bookings = (List<BookingDto>) request.getAttribute("bookings");
                for(BookingDto booking : bookings){
                %>
                <div class="booking-card" data-booking-id="<%=booking.getId()%>">
                    <div class="booking-thumb">
                        <img src="<%=booking.getRoom().getPicture()%>" alt="Номер: <%=booking.getRoom().getNumber()%>">
                    </div>
                    <div class="booking-info">
                        <div class="booking-header">
                            <div class="room-name">Номер: <%=booking.getRoom().getNumber()%></div>
                            <%BookingStatus status = booking.getStatus();%>
                            <div class="booking-status <%=status==BookingStatus.ACTIVE ? "confirmed" : status==BookingStatus.COMPLETED ? "completed" : "cancelled"%>"><%=status==BookingStatus.ACTIVE ? "Активно" : status==BookingStatus.COMPLETED ? "Завершено" : "Отменено"%></div>
                        </div>
                        <div class="booking-details">
                            <span class="detail-item">📅 <%=booking.getCheckInDate()%> — <%=booking.getCheckOutDate()%></span>
                            <span class="detail-item">👥 <%=booking.getGuests().size()%> гостя</span>
                        </div>
                        <div class="booking-address">🏢 <%=booking.getRoom().getBuilding().getName()%>, <%=booking.getRoom().getFloor()%> этаж</div>
                        <div class="booking-footer">
                            <div class="booking-price"><%=booking.getTotalPrice()%></div>
                            <div class="booking-actions">
                                <a href="/hotelsystem/booking/details/<%=booking.getId()%>" class="action-btn">Подробнее</a>
                                <form method="post" action="" class="cancel-form">
                                    <button type="submit" class="action-btn cancel" data-booking-id="<%=booking.getId()%>" <%=status==BookingStatus.CANCELED ? "disabled style=\"opacity: 0.4; cursor: not-allowed;\"" : ""%> >Отменить</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <%}%>
            </div>

            <!-- Пагинация -->
            <div class="pagination">
                <%Integer total = (Integer) request.getAttribute("totalPages");
                Integer currentPage = (Integer) request.getAttribute("page");
                String params = request.getQueryString();
                %>
                <%for(int i = 0; i < currentPage; i++){%>
                <a href="<%=(params==null || params.isEmpty()) ? "?" : "?"+params+"&"%>page=<%=i%>" class="page-item"><%=i+1%></a>
                <%}%>
                <a href="#" class="page-item active"><%=currentPage+1%></a>
                <%for(int i = currentPage; i < total-1; i++){%>
                <a href="<%=(params==null || params.isEmpty()) ? "?" : "?"+params+"&"%>page=<%=i%>" class="page-item"><%=i+1%></a>
                <%}%>
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

        <div class="footer-placeholder">
            🌿 HavenStay — тишина, комфорт и забота о каждом госте
        </div>
    </div>
</body>
</html>