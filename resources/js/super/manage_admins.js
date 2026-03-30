const confirmModal     = document.getElementById("confirmModal");
const confirmModalOk   = document.getElementById("confirmModalOk");
const confirmModalCancel = document.getElementById("confirmModalCancel");
const confirmModalText  = document.getElementById("confirmModalText");

function confirm(question){
    return new Promise((resolve) => {
        confirmModalText.textContent = question;
        confirmModal.showModal();

        const onConfirm = () => {
            confirmModal.close();
            confirmModalOk.removeEventListener("click", onConfirm);
            confirmModalCancel.removeEventListener("click", onCancel);
            resolve(true);
        };

        const onCancel = () => {
            confirmModal.close();
            confirmModalOk.removeEventListener("click", onConfirm);
            confirmModalCancel.removeEventListener("click", onCancel);
            resolve(false);
        };

        confirmModalOk.addEventListener("click", onConfirm);
        confirmModalCancel.addEventListener("click", onCancel);
    });
}

const infoModal = document.getElementById("infoModal");
const infoModalOk = document.getElementById("infoModalOk");
const infoModalText = document.getElementById("infoModalText");

function info(infotmation){
    return new Promise((resolve) =>{
        infoModalText.textContent = infotmation;
        infoModal.showModal();

        const onOK = () =>{
            infoModal.close();
            infoModalOk.removeEventListener("click", onOK);
            resolve();
        };

        infoModalOk.addEventListener("click", onOK);
    });
}

// Добавление нового администратора
const addAdminForm = document.getElementById('addAdminForm');
addAdminForm.addEventListener('submit',async function(e) {
    e.preventDefault();
    const formData = new FormData(addAdminForm);
    const searchParams = new URLSearchParams(formData);
    const response = await fetch(`/hotelsystem/super/admin/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: searchParams,
    });
    if(response.ok){
        await info("Администратор добавлен");
    }else if(response.status == 409){
        await info("Такой логин уже существует");
    }else{
        await info("Что-то не так :(");
    }
});

// Функция для редактирования строки
function enterEditMode(row) {
    const loginCell = row.querySelector('.login-cell');
    const passwordCell = row.querySelector('.password-cell');
    const fullnameCell = row.querySelector('.fullname-cell');
    const phoneCell = row.querySelector('.phone-cell');
    const actionsCell = row.querySelector('.actions-cell');
    
    const currentLogin = loginCell.textContent;
    const currentFullname = fullnameCell.textContent;
    const currentPhone = phoneCell.textContent;
    
    loginCell.innerHTML = `<input type="text" class="editable-input" value="${currentLogin}" id="edit_login" required>`;
    passwordCell.innerHTML = `<input type="password" class="editable-input" value="" placeholder="Новый пароль" id="edit_password" required>`;
    fullnameCell.innerHTML = `<input type="text" class="editable-input" value="${currentFullname}" id="edit_fullname" required>`;
    phoneCell.innerHTML = `<input type="tel" class="editable-input" value="${currentPhone}" id="edit_phone" required>`;
    
    actionsCell.innerHTML = `
        <button class="action-link save" data-action="save">Сохранить</button>
        <button class="action-link cancel" data-action="cancel">Отмена</button>
    `;
    
    const saveBtn = actionsCell.querySelector('[data-action="save"]');
    const cancelBtn = actionsCell.querySelector('[data-action="cancel"]');
    
    saveBtn.addEventListener('click', () => saveEdit(row, currentLogin, currentFullname, currentPhone));
    cancelBtn.addEventListener('click', () => cancelEdit(row, currentLogin, currentFullname, currentPhone));
}

async function saveEdit(row, currentLogin, currentFullname, currentPhone) {
    const adminId = row.getAttribute('data-admin-id');
    const newLogin = row.querySelector('#edit_login').value;
    const newPassword = row.querySelector('#edit_password').value;
    const newFullname = row.querySelector('#edit_fullname').value;
    const newPhone = row.querySelector('#edit_phone').value;

    const form = new URLSearchParams();
    form.append("login", newLogin);
    form.append("password", newPassword);
    form.append("fullname", newFullname);
    form.append("phone", newPhone);

    const response = await fetch(`/hotelsystem/super/admin/update/${adminId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: form,
    });
    if(response.ok){
        await info("Данные изменены");
    }else if(response.status == 409){
        await info("Такой логин уже существует");
        cancelEdit(row, currentLogin, currentFullname, currentPhone);
        return;
    }else{
        await info("Что-то не так :(");
        cancelEdit(row, currentLogin, currentFullname, currentPhone);
        return;
    }
    
    row.querySelector('.login-cell').textContent = newLogin;
    row.querySelector('.fullname-cell').textContent = newFullname;
    row.querySelector('.phone-cell').textContent = newPhone;
    row.querySelector('.password-cell').textContent = '••••••••';
    
    const actionsCell = row.querySelector('.actions-cell');
    actionsCell.innerHTML = `
        <button class="action-link edit" data-action="edit">Редактировать</button>
        <button class="action-link delete" data-action="delete">Удалить</button>
    `;
    
    const editBtn = actionsCell.querySelector('[data-action="edit"]');
    const deleteBtn = actionsCell.querySelector('[data-action="delete"]');
    
    editBtn.addEventListener('click', () => enterEditMode(row));
    deleteBtn.addEventListener('click', () => deleteAdmin(row));
}

function cancelEdit(row, currentLogin, currentFullname, currentPhone) {
    row.querySelector('.login-cell').textContent = currentLogin;
    row.querySelector('.password-cell').textContent = '••••••••';
    row.querySelector('.fullname-cell').textContent = currentFullname;
    row.querySelector('.phone-cell').textContent = currentPhone;
    
    const actionsCell = row.querySelector('.actions-cell');
    actionsCell.innerHTML = `
        <button class="action-link edit" data-action="edit">Редактировать</button>
        <button class="action-link delete" data-action="delete">Удалить</button>
    `;
    
    const editBtn = actionsCell.querySelector('[data-action="edit"]');
    const deleteBtn = actionsCell.querySelector('[data-action="delete"]');
    
    editBtn.addEventListener('click', () => enterEditMode(row));
    deleteBtn.addEventListener('click', () => deleteAdmin(row));
}

async function deleteAdmin(row){
    const adminId = row.getAttribute('data-admin-id');
    if(!await confirm('Вы точно хотите удалить этого администратора?')){
        return;
    }
    const response = await fetch(`/hotelsystem/super/admin/delete/${adminId}`, {
        method: "POST"
    });
    if(response.ok){
        row.remove();
        await info("Администратор удален");
    }else{
        await info("Что-то не так :(");
    }
}

// Инициализация обработчиков для существующих строк
const rows = document.querySelectorAll('#adminsTable tbody tr');
rows.forEach(row => {
    const editBtn = row.querySelector('[data-action="edit"]');
    const deleteBtn = row.querySelector('[data-action="delete"]');
    if (editBtn) {
        editBtn.addEventListener('click', () => enterEditMode(row));
    }
    if (deleteBtn) {
        deleteBtn.addEventListener('click', () => deleteAdmin(row));
    }
});