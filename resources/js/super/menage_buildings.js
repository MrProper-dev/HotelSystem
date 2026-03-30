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

// Функция для редактирования строки
function enterEditMode(row) {
    const nameCell = row.querySelector('.name-cell');
    const addressCell = row.querySelector('.address-cell');
    const floorsCell = row.querySelector('.floors-cell');
    const actionsCell = row.querySelector('.actions-cell');
    
    const currentName = nameCell.textContent;
    const currentAddress = addressCell.textContent;
    const currentFloors = floorsCell.textContent;
    
    nameCell.innerHTML = `<input type="text" class="editable-input" value="${currentName}" id="edit_name" required>`;
    addressCell.innerHTML = `<input type="text" class="editable-input" value="${currentAddress}" id="edit_address" required>`;
    floorsCell.innerHTML = `<input type="number" class="editable-input" value="${currentFloors}" id="edit_floors" min="1" required>`;
    
    actionsCell.innerHTML = `
        <button class="action-link save" data-action="save">Сохранить</button>
        <button class="action-link cancel" data-action="cancel">Отмена</button>
    `;
    
    const saveBtn = actionsCell.querySelector('[data-action="save"]');
    const cancelBtn = actionsCell.querySelector('[data-action="cancel"]');
    
    saveBtn.addEventListener('click', () => saveEdit(row));
    cancelBtn.addEventListener('click', () => cancelEdit(row, currentName, currentAddress, currentFloors));
}

async function saveEdit(row) {
    const buildingId = row.getAttribute('data-building-id');
    const newName = row.querySelector('#edit_name').value;
    const newAddress = row.querySelector('#edit_address').value;
    const newFloors = row.querySelector('#edit_floors').value;
    
    row.querySelector('.name-cell').textContent = newName;
    row.querySelector('.address-cell').textContent = newAddress;
    row.querySelector('.floors-cell').textContent = newFloors;
    
    const actionsCell = row.querySelector('.actions-cell');
    actionsCell.innerHTML = `
        <button class="action-link edit" data-action="edit">Редактировать</button>
        <button class="action-link delete" data-action="delete" data-building-name="${newName}">Удалить</button>
    `;
    
    const editBtn = actionsCell.querySelector('[data-action="edit"]');
    const deleteBtn = actionsCell.querySelector('[data-action="delete"]');
    
    editBtn.addEventListener('click', () => enterEditMode(row));
    deleteBtn.addEventListener('click', () => openDeleteModal(row, newName));
    
    const searchParams = new URLSearchParams();
    searchParams.append("name", newName);
    searchParams.append("address", newAddress);
    searchParams.append("floors", newFloors);

    const response = await fetch(`/hotelsystem/super/building/update/${buildingId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: searchParams,
    });
    if(response.ok){
        await info("Данные изменены");
    }else{
        await info("Что-то не так :(");
    }
}

function cancelEdit(row, originalName, originalAddress, originalFloors) {
    row.querySelector('.name-cell').textContent = originalName;
    row.querySelector('.address-cell').textContent = originalAddress;
    row.querySelector('.floors-cell').textContent = originalFloors;
    
    const actionsCell = row.querySelector('.actions-cell');
    actionsCell.innerHTML = `
        <button class="action-link edit" data-action="edit">Редактировать</button>
        <button class="action-link delete" data-action="delete" data-building-name="${originalName}">Удалить</button>
    `;
    
    const editBtn = actionsCell.querySelector('[data-action="edit"]');
    const deleteBtn = actionsCell.querySelector('[data-action="delete"]');
    
    editBtn.addEventListener('click', () => enterEditMode(row));
    deleteBtn.addEventListener('click', () => openDeleteModal(row, originalName));
}

// Инициализация обработчиков для существующих строк
const rows = document.querySelectorAll('#buildingsTable tbody tr');
rows.forEach(row => {
    const editBtn = row.querySelector('[data-action="edit"]');
    const deleteBtn = row.querySelector('[data-action="delete"]');
    
    if (editBtn) {
        editBtn.addEventListener('click', () => enterEditMode(row));
    }
    if (deleteBtn) {
        deleteBtn.addEventListener('click', () => deletebuilding(row));
    }
});

// Удаление корпуса
async function deletebuilding(row) {
    if(!await confirm('Вы точно хотите удалить корпус? Вместе с ним удалятся его комнаты!')){
        return;
    }
    const buildingId = row.getAttribute('data-building-id');
    const response = await fetch(`/hotelsystem/super/building/delete/${buildingId}`, {
        method: "POST"
    });
    if(response.ok){
        await info("Корпус и его комнаты удалены");
        row.remove();
    }else{
        await info("Что-то не так :(");
    }
}

// Добавление нового корпуса
const addBuildingForm = document.getElementById('addBuildingForm');
addBuildingForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = new FormData(addBuildingForm);
    const searchParams = new URLSearchParams(formData);
    const response = await fetch(`/hotelsystem/super/building/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: searchParams,
    });
    if(response.ok){
        await info("Корпус добавлен");
    }else{
        await info("Что-то не так :(");
    }
});