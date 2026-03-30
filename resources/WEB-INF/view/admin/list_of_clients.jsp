<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Администратор · Список клиентов</title>
    <link rel="stylesheet" href="/hotelsystem/css/admin/list_of_clients.css">
    <script src="/hotelsystem/js/admin/list_of_clients.js" defer></script>
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
                    <a href="#" class="nav-link active">Клиенты</a>
                </div>
            </div>
            <form action="/hotelsystem/admin/logout" method="get" class="logout-form">
                <button type="submit" class="nav-link logout-link">Выйти из аккаунта</button>
            </form>
        </div>

        <div class="page-header">
            <div class="page-title">Список клиентов</div>
        </div>

        <!-- Поиск клиентов -->
        <div class="search-section">
            <div class="search-header">Поиск клиентов</div>
            <form class="search-form" method="GET" action="" id="searchForm">
                <div class="search-grid">
                    <div class="search-item">
                        <div class="search-label">ФИО</div>
                        <div class="search-field">
                            <input type="text" name="name" placeholder="Иванов Иван" value="">
                        </div>
                    </div>
                    <div class="search-item">
                        <div class="search-label">Номер телефона</div>
                        <div class="search-field">
                            <input type="tel" name="phone" placeholder="+7 (___) ___-__-__" value="">
                        </div>
                    </div>
                    <div class="search-item">
                        <div class="search-label">Email</div>
                        <div class="search-field">
                            <input type="text" name="email" placeholder="example@mail.com" value="">
                        </div>
                    </div>
                    <button type="submit" class="search-button">Найти</button>
                </div>
            </form>
        </div>

        <div class="clients-header">
            <div class="clients-header-left">Зарегистрированные клиенты</div>
            <div class="clients-header-right">найдено клиентов: ${clientsCount}</div>
        </div>

        <!-- Таблица клиентов -->
        <table class="clients-table">
            <thead>
                <tr>
                    <th>Имя</th>
                    <th>Телефон</th>
                    <th>Email</th>
                    <th>Статус</th>
                    <th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <%
                for(ClientDto client : (List<ClientDto>) request.getAttribute("clients")){
                    pageContext.setAttribute("client", client);
                %>
                <tr data-user-id="1" data-user-name="Иванов Иван Иванович">
                    <td>${client.name}</td>
                    <td>${client.phone}</td>
                    <td>${client.email}</td>
                    <td><span class="client-status ${client.blocked == 'true' ? 'blocked' : ''}">${client.blocked == 'true' ? 'заблокирован' : 'активен'}</span></td>
                    <td>
                        <a href="/hotelsystem/admin/client/details/${client.id}" class="action-link" data-action="profile">Профиль</a>
                        <button type="button" class="action-link ${client.blocked == 'true' ? '' : 'block'} blockBtn" data-client-id="${client.id}">${client.blocked == 'true' ? 'Разблокировать' : 'Заблокировать'}</button>
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
            HavenStay — система управления клиентами
        </div>
    </div>
</body>
</html>