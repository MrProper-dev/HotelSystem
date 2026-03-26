<%@ page 
    contentType="text/html;charset=UTF-8"
%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отель · Личный кабинет</title>
    <link rel="stylesheet" href="/hotelsystem/css/client/profile.css">
    <script src="/hotelsystem/js/client/profile.js" defer></script>
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
                    <a href="#" class="nav-link active">Профиль</a>
                </div>
            </div>
        </div>

        <div class="page-header">
            <div class="page-title">👤 Личный кабинет</div>
            <a class="logout-link" id="logoutBtn">🚪 Выйти из аккаунта</a>
        </div>

        <div class="profile-container">
            <form method="POST" action="" id="profileForm">
                <div class="profile-section">
                    <div class="section-label">Данные аккаунта</div>
                    <div class="fields-grid">
                        <div class="field-group">
                            <div class="field-label">Электронная почта (логин)</div>
                            <div class="field-value editable">
                                <input type="email" name="email" value="${client.email}" required>
                            </div>
                        </div>
                        <div class="field-group">
                            <div class="field-label">Пароль</div>
                            <div class="field-value editable">
                                <input type="password" name="password" placeholder="••••••••" value="">
                            </div>
                            <div class="field-note">Оставьте пустым, если не хотите менять</div>
                        </div>
                        <div class="field-group">
                            <div class="field-label">Имя</div>
                            <div class="field-value editable">
                                <input type="text" name="name" value="${client.name}" required>
                            </div>
                        </div>
                        <div class="field-group">
                            <div class="field-label">Номер телефона</div>
                            <div class="field-value editable">
                                <input type="tel" name="phone" value="${client.phone}" required>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Кнопки действий -->
                <div class="action-buttons">
                    <button type="button" class="button" id="cancelBtn">Отмена</button>
                    <button type="submit" class="button primary">Сохранить изменения</button>
                </div>
            </form>
        </div>

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

        <!-- модальное окно (confirm) -->
        <dialog id="confirmModal" class="modal-dialog">
            <div class="modal-content">
                <div class="modal-body">
                    <p id="confirmModalText">Подтвердите действие.</p>
                </div>
                <div class="modal-footer confirm-buttons">
                    <button id="confirmModalCancel" class="button-primary">Назад</button>
                    <button id="confirmModalOk" class="button-primary">Выйти</button>
                </div>
            </div>
        </dialog>

        <div class="footer-placeholder">
            🌿 HavenStay — тишина, комфорт и забота о каждом госте
        </div>
    </div>
</body>
</html>