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

// Обновление этажей
const buildingSelect = document.querySelector('select[name="building"]');
const floorSelect = document.querySelector('select[name="floor"]');
buildingSelect.addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const maxFloors = parseInt(selectedOption.dataset.buildingFloors);
    floorSelect.innerHTML = '';
    while (floorSelect.options.length < maxFloors) {
        const newFloorNumber = floorSelect.options.length + 1;
        const option = document.createElement('option');
        option.value = newFloorNumber;
        option.textContent = `${newFloorNumber} этаж`;
        floorSelect.appendChild(option);
    }
});

// Инициализация обработчиков для существующих кнопок удаления
const deleteButtons = document.querySelectorAll('.action-link.delete');
deleteButtons.forEach(btn => {
    btn.addEventListener('click', async function(e) {
        const row = this.closest('tr');
        const roomId = row.dataset.roomId;
        if(!await confirm('Вы точно хотите удалить комнату?')){
            return;
        }
        const response = await fetch(`/hotelsystem/super/room/delete/${roomId}`, {
            method: "POST"
        });
        if(response.ok){
            await info("Команта удалена");
            row.remove();
        }else{
            await info("Что-то не так :(");
        }
    });
});

// Добавление новой комнаты
const addRoomForm = document.getElementById('addRoomForm');
addRoomForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    const formData = new FormData(addRoomForm);
    const searchParams = new URLSearchParams(formData);
    const response = await fetch(`/hotelsystem/super/room/add`, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
        },
        body: searchParams,
    });
    if(response.ok){
        await info("Комната добавлена");
    }else{
        await info("Что-то не так :(");
    }
});