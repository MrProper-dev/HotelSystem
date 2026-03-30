<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Администратор · Номерной фонд</title>
    <link rel="stylesheet" href="/hotelsystem/css/admin/list_of_rooms.css">
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="#" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">admin</span></span>
                </a>
                <div class="nav-links">
                    <a href="#" class="nav-link active">Номерной фонд</a>
                    <a href="/hotelsystem/admin/clients" class="nav-link">Клиенты</a>
                </div>
            </div>
            <form action="/hotelsystem/admin/logout" method="get" class="logout-form">
                <button type="submit" class="nav-link logout-link">Выйти из аккаунта</button>
            </form>
        </div>

        <div class="page-header">
            <div class="page-title">Номерной фонд</div>
        </div>

        <!-- Фильтры -->
        <div class="filters-section">
            <div class="filter-header">Фильтры номеров</div>
            <form class="filter-form" method="GET" action="">
                <div class="filter-grid">
                    <!-- даты -->
                    <div class="filter-item">
                        <div class="filter-label">Дата заезда</div>
                        <div class="filter-field">
                            <input type="date" name="checkin" value="">
                        </div>
                    </div>
                    <div class="filter-item">
                        <div class="filter-label">Дата выезда</div>
                        <div class="filter-field">
                            <input type="date" name="checkout" value="">
                        </div>
                    </div>
                    <!-- спальных мест -->
                    <div class="filter-item">
                        <div class="filter-label">Кол-во мест</div>
                        <div class="filter-field small">
                            <select name="guests">
                                <option value="">Любое</option>
                                <%for(int i = 1; i<(Integer)request.getAttribute("maxGuests")+1; i++){%>
                                <option value="<%=i%>">Гостей: <%=i%></option>
                                <%}%>
                            </select>
                        </div>
                    </div>
                    <!-- этаж -->
                    <div class="filter-item">
                        <div class="filter-label">Этаж</div>
                        <div class="filter-field small">
                            <select name="floor">
                                <option value="">Любой</option>
                                <%for(int i = 1; i<(Integer)request.getAttribute("maxFloor")+1; i++){%>
                                <option value="<%=i%>"><%=i%> этаж</option>
                                <%}%>
                            </select>
                        </div>
                    </div>
                    <!-- корпус -->
                    <div class="filter-item">
                        <div class="filter-label">Корпус</div>
                        <div class="filter-field">
                            <select name="building">
                                <option value="">Все корпуса</option>
                                <%for(BuildingDto building : (List<BuildingDto>)request.getAttribute("buildings")){%>
                                <option value="<%=building.getId()%>"><%=building.getName()%></option>
                                <%}%>
                            </select>
                        </div>
                    </div>
                    <!-- цена -->
                    <div class="filter-item">
                        <div class="filter-label">Цена за сутки</div>
                        <div class="price-range">
                            <div class="price-input">
                                <input type="number" name="price_min" placeholder="от" value="">
                            </div>
                            <span>—</span>
                            <div class="price-input">
                                <input type="number" name="price_max" placeholder="до" value="">
                            </div>
                        </div>
                    </div>
                    <!-- статус брони -->
                    <div class="filter-item">
                        <div class="filter-label">Статус номера</div>
                        <div class="filter-field">
                            <select name="status">
                                <option value="">Все номера</option>
                                <option value="FREE">Свободны</option>
                                <option value="BUSY">Заняты</option>
                            </select>
                        </div>
                    </div>
                    <!-- кнопка применить -->
                    <div class="filter-item">
                        <button type="submit" class="apply-filter">Применить</button>
                    </div>
                </div>
            </form>
        </div>

        <div class="rooms-header">
            <div class="rooms-header-left">Список комнат</div>
            <div class="rooms-header-right">найдено: ${roomsCount} комнат</div>
        </div>

        <!-- СПИСОК КОМНАТ -->
        <div class="rooms-list">
            <!-- Заголовки колонок -->
            <div class="room-row" style="background: #F9F7F3;">
                <div class="room-field label">Номер</div>
                <div class="room-field label">Характеристики</div>
                <div class="room-field label">Корпус</div>
                <div class="room-field label">Цена за сутки</div>
                <div class="room-field label">Статус</div>
                <div class="room-field label">Действия</div>
            </div>

            <!-- Карточка -->
            <%
            List<RoomDto> rooms = (List<RoomDto>) request.getAttribute("rooms");
            for(int i = 0; i < rooms.size(); i++){
                RoomDto room = rooms.get(i);
            %>
            <div class="room-row">
                <div class="room-field"><%=room.getNumber()%></div>
                <div class="room-field">Мест: <%=room.getSleepingPlaces()%> Этаж: <%=room.getFloor()%></div>
                <div class="room-field"><%=room.getBuilding().getName()%></div>
                <div class="room-field"><%=room.getPrice()%> ₽</div>
                <div class="status-badge <%=room.getStatus() == null ? "none" : room.getStatus() == RoomStatus.FREE ? "active" : "occupied"%>"><%=room.getStatus() == null ? "Даты не указаны" : room.getStatus() == RoomStatus.FREE ? "Свободен" : "Занят"%></div>
                <a href="/hotelsystem/admin/rooms/<%=room.getId()%>" class="action-link">Подробнее</a>
            </div>
            <%}%>
        </div>

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

        <div class="footer-placeholder">
            HavenStay — система управления номерным фондом
        </div>
    </div>
</body>
</html>