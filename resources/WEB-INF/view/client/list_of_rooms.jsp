<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.BuildingDto,java.util.*,dtos.RoomDto"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отель · Список номеров</title>
    <link rel="stylesheet" href="css/client/list_of_rooms.css">
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="#" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">hotel</span></span>
                </a>
                <div class="nav-links">
                    <a href="#" class="nav-link active">Комнаты</a>
                    <a href="/hotelsystem/booking/history" class="nav-link">Мои брони</a>
                    <a href="/hotelsystem/profile" class="nav-link">Профиль</a>
                </div>
            </div>
        </div>

        <!-- Форма фильтров -->
        <div class="filters-section">
            <div class="filter-header">📅 Найти идеальный номер</div>
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
                    <!-- кол-во мест (гостей) -->
                    <div class="filter-item">
                        <div class="filter-label">Кол-во гостей</div>
                        <div class="filter-field">
                            <select name="guests">
                                <option value="">Любое</option>
                                <%for(int i = 1; i<(Integer)request.getAttribute("maxGuests")+1; i++){%>
                                <option value="<%=i%>"><%=i%> гость</option>
                                <%}%>
                            </select>
                        </div>
                    </div>
                    <!-- этаж -->
                    <div class="filter-item">
                        <div class="filter-label">Этаж</div>
                        <div class="filter-field">
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
                    <!-- цена от/до -->
                    <div class="filter-item">
                        <div class="filter-label">Цена / сутки</div>
                        <div class="price-range">
                            <div class="price-input">
                                <input type="number" name="price_min" placeholder="от">
                            </div>
                            <span>—</span>
                            <div class="price-input">
                                <input type="number" name="price_max" placeholder="до">
                            </div>
                        </div>
                    </div>
                    <!-- кнопка отправки формы -->
                    <div class="filter-item">
                        <button type="submit" class="apply-filter">Применить фильтры</button>
                    </div>
                </div>
            </form>
        </div>

        <div class="rooms-header">
            <div class="rooms-header-left">✨ Наши номера</div>
            <div class="rooms-header-right">найдено: ${roomsCount} вариантов</div>
        </div>

        <!-- карточки -->
        <div class="cards-grid">

            <%for(RoomDto room : (List<RoomDto>)request.getAttribute("rooms")){%>
            <div class="room-card">
                <div class="room-image">
                    <img src="<%=room.getPicture()%>" alt="Фото номера: <%=room.getNumber()%>">
                </div>
                <div class="room-general">Номер: <%=room.getNumber()%></div>
                <div class="attrs">
                    <span class="attr">🛌 <%=room.getSleepingPlaces() == 1 ? room.getSleepingPlaces()+" место" : room.getSleepingPlaces() <= 4 ? room.getSleepingPlaces()+" места" : room.getSleepingPlaces()+" мест"%></span>
                    <span class="attr">🏢 <%=room.getFloor()%> этаж</span>
                    <span class="attr">🏛️ <%=room.getBuilding().getName()%></span>
                </div>
                <div class="price-placeholder"><%=room.getPrice()%></div>
                <a href="/hotelsystem/rooms/<%=room.getId()%>" class="details-button">Подробнее о номере</a>
            </div>
            <%}%>

        </div>

        <!-- ПАГИНАЦИЯ (страницы 1–4) -->
        <div class="pagination">
            <%
            Integer currentPage = (Integer)request.getAttribute("currentPage");
            String params = request.getQueryString();
            %>
            <%for(int i = 1; i<currentPage; i++){%>
            <a href="<%=(params==null || params.isEmpty()) ? "?" : "?"+params+"&"%>page=<%=i%>" class="page-link"><%=i%></a>
            <%}%>
            <a href="<%=(params==null || params.isEmpty()) ? "?" : "?"+params+"&"%>page=1" class="page-link active-page">${currentPage}</a>
            <%for(int i = currentPage+1; i<=(Integer)request.getAttribute("pageCount"); i++){%>
            <a href="<%=(params==null || params.isEmpty()) ? "?" : "?"+params+"&"%>page=<%=i%>" class="page-link"><%=i%></a>
            <%}%>
        </div>

        <div class="footer-placeholder">
            🌿 HavenStay — тишина, комфорт и забота о каждом госте
        </div>
    </div>

</body>
</html>