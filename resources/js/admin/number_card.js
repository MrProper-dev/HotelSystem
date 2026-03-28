const infoModal    = document.getElementById("infoModal");
const infoModalOk  = document.getElementById("infoModalOk");
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

// Загрузка фото
const photoPlaceholder = document.getElementById('photoPlaceholder');
const fileInput = document.getElementById('fileInput');
const roomImage = document.getElementById('roomImage');
photoPlaceholder.addEventListener('click', function() {
    fileInput.click();
});
fileInput.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(event) {
            roomImage.src = event.target.result;
            photoPlaceholder.classList.remove('no-image');
        };
        reader.readAsDataURL(file);
    }
});

// Обновление этажей
const buildingSelect = document.querySelector('select[name="building"]');
const floorSelect = document.querySelector('select[name="floor"]');
buildingSelect.addEventListener('change', function() {
    const selectedOption = this.options[this.selectedIndex];
    const maxFloors = parseInt(selectedOption.dataset.buildingFloors) || 0;
    const currentFloorValue = floorSelect.value;
    const currentFloor = parseInt(currentFloorValue) || 1;
    while (floorSelect.options.length > maxFloors) {
        floorSelect.remove(floorSelect.options.length - 1);
    }
    while (floorSelect.options.length < maxFloors) {
        const newFloorNumber = floorSelect.options.length + 1;
        const option = document.createElement('option');
        option.value = newFloorNumber;
        option.textContent = getFloorText(newFloorNumber)`${newFloorNumber} этаж`;
        floorSelect.appendChild(option);
    }
    const availableFloor = Math.min(currentFloor, maxFloors);
    floorSelect.value = availableFloor;
});

// Обработка отправки формы
const roomForm = document.getElementById('roomForm');
roomForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const roomId = roomForm.dataset.roomId;
    const formData = new FormData(roomForm);
    const response = await fetch(`/hotelsystem/admin/room/update/${roomId}`, {
        method: 'POST',
        body: formData
    });
    
    if (response.ok) {
        await info('Комната успешно обновлена!');
    } else {
        await info('Что-то не так :(');
    }
});