<%@ page 
    contentType="text/html;charset=UTF-8"
    import="dtos.*,java.util.*,java.time.*,java.time.temporal.*"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Суперпользователь · Управление администраторами</title>
    <link rel="stylesheet" href="/hotelsystem/css/super/manage_admins.css">
    <script src="/hotelsystem/js/super/manage_admins.js" defer></script>
</head>
<body>
    <div class="app-container">
        <!-- Верхняя навигация -->
        <div class="top-nav">
            <div class="logo-block">
                <a href="#" class="logo-placeholder" style="text-decoration: none;">
                    <span class="logo-icon">🏡</span>
                    <span class="logo-text">HavenStay<span class="logo-accent">super</span></span>
                </a>
                <div class="nav-links">
                    <a href="#" class="nav-link">Комнаты</a>
                    <a href="#" class="nav-link">Корпуса</a>
                    <a href="#" class="nav-link active">Администраторы</a>
                </div>
            </div>
        </div>

        <div class="page-header">
            <div class="page-title">Управление администраторами</div>
        </div>

        <!-- Форма добавления нового администратора -->
        <div class="add-section">
            <div class="add-header">Добавление нового администратора</div>
            <form class="add-form" id="addAdminForm" method="POST" action="">
                <div class="form-field">
                    <div class="field-label">Логин</div>
                    <div class="field-input">
                        <input type="text" name="login" placeholder="admin123" required>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">Пароль</div>
                    <div class="field-input">
                        <input type="password" name="password" placeholder="password123" required>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">ФИО</div>
                    <div class="field-input">
                        <input type="text" name="fullname" placeholder="Иван Иванов" required>
                    </div>
                </div>
                <div class="form-field">
                    <div class="field-label">Номер телефона</div>
                    <div class="field-input">
                        <input type="tel" name="phone" placeholder="+7 (900) 123-45-67" required>
                    </div>
                </div>
                <button type="submit" class="add-button">Добавить администратора</button>
            </form>
        </div>

        <!-- Поиск администраторов -->
        <div class="search-section">
            <div class="search-header">Поиск администраторов</div>
            <form class="search-form" id="searchForm" method="GET" action="">
                <div class="search-grid">
                    <div class="search-item">
                        <div class="search-label">Логин</div>
                        <div class="search-field">
                            <input type="text" name="login" placeholder="login_example">
                        </div>
                    </div>
                    <div class="search-item">
                        <div class="search-label">ФИО</div>
                        <div class="search-field">
                            <input type="text" name="fullname" placeholder="Иванов Иван">
                        </div>
                    </div>
                    <div class="search-item">
                        <div class="search-label">Номер телефона</div>
                        <div class="search-field">
                            <input type="tel" name="phone" placeholder="+7 (___) ___-__-__">
                        </div>
                    </div>
                    <button type="submit" class="search-button">Найти</button>
                </div>
            </form>
        </div>

        <div class="list-header">
            <div class="list-header-left">Список администраторов</div>
            <div class="list-header-right" id="adminsCount">Всего администраторов: ${adminsCount}</div>
        </div>

        <!-- Таблица администраторов -->
        <table class="admins-table" id="adminsTable">
            <thead>
                <tr>
                    <th>Логин</th>
                    <th>Пароль</th>
                    <th>ФИО</th>
                    <th>Номер телефона</th>
                    <th class="actions-cell">Действия</th>
                  </thead>
            <tbody>
                <%
                for(AdminDto admin : (List<AdminDto>) request.getAttribute("admins")){
                    pageContext.setAttribute("admin", admin);
                %>
                <tr data-admin-id="${admin.id}">
                    <td class="login-cell">${admin.login}</td>
                    <td class="password-cell">••••••••</td>
                    <td class="fullname-cell">${admin.fullName}</td>
                    <td class="phone-cell">${admin.phone}</td>
                    <td class="actions-cell">
                        <button class="action-link edit" data-action="edit">Редактировать</button>
                        <button class="action-link delete" data-action="delete">Удалить</button>
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
            HavenStay — система управления администраторами
        </div>
    </div>
</body>
</html>