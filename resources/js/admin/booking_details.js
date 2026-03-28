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

// Динамическое добавление и удаление гостей
let guestCounter = 2;
const guestsContainer = document.getElementById('guestsContainer');
const addGuestBtn = document.getElementById('addGuestBtn');
const guestsCountSpan = document.getElementById('guestsCount');

function updateGuestsCount() {
    const guestCards = document.querySelectorAll('#guestsContainer .guest-card');
    const count = guestCards.length;
    const text = getGuestsText(count);
    guestsCountSpan.textContent = text;
}

function getGuestsText(count) {
    if (count === 1) return '1 гость';
    if (count >= 2 && count <= 4) return `${count} гостя`;
    return `${count} гостей`;
}

function createGuestCard(index) {
    const guestCard = document.createElement('div');
    guestCard.className = 'guest-card';
    guestCard.setAttribute('data-guest-id', index);
    
    guestCard.innerHTML = `
        <div class="guest-header">
            <div class="guest-title">Гость ${index + 1}</div>
            <button type="button" class="remove-guest" data-guest-index="${index}">Удалить</button>
        </div>
        <div class="guest-fields">
            <div class="field-group">
                <div class="field-label">Фамилия</div>
                <input type="text" class="field-input" name="guests[${index}][last_name]" placeholder="Иванов" required>
            </div>
            <div class="field-group">
                <div class="field-label">Имя</div>
                <input type="text" class="field-input" name="guests[${index}][first_name]" placeholder="Иван" required>
            </div>
            <div class="field-group">
                <div class="field-label">Отчество</div>
                <input type="text" class="field-input" name="guests[${index}][surname]" placeholder="Иванович">
            </div>
            <div class="field-group">
                <div class="field-label">Дата рождения</div>
                <input type="date" class="field-input" name="guests[${index}][birthdate]" required>
            </div>
            <div class="field-group">
                <div class="field-label">Серия пасп. / свид. о рожд.</div>
                <input type="text" class="field-input" name="guests[${index}][doc_series]" placeholder="1234" required>
            </div>
            <div class="field-group">
                <div class="field-label">Номер пасп. / свид. о рожд.</div>
                <input type="text" class="field-input" name="guests[${index}][doc_number]" placeholder="567890" required>
            </div>
        </div>
    `;
    
    const removeBtn = guestCard.querySelector('.remove-guest');
    removeBtn.addEventListener('click', function() {
        guestCard.remove();
        reindexGuests();
        updateGuestsCount();
    });
    
    return guestCard;
}

function reindexGuests() {
    const guestCards = document.querySelectorAll('#guestsContainer .guest-card');
    guestCards.forEach((card, idx) => {
        const newIndex = idx;
        const titleDiv = card.querySelector('.guest-title');
        titleDiv.textContent = `Гость ${newIndex + 1}`;
        card.setAttribute('data-guest-id', newIndex);
        
        const inputs = card.querySelectorAll('input');
        inputs.forEach(input => {
            const name = input.getAttribute('name');
            if (name) {
                const newName = name.replace(/guests\[\d+\]/, `guests[${newIndex}]`);
                input.setAttribute('name', newName);
            }
        });
        
        const removeBtn = card.querySelector('.remove-guest');
        if (removeBtn) {
            removeBtn.setAttribute('data-guest-index', newIndex);
            const newRemoveBtn = removeBtn.cloneNode(true);
            removeBtn.parentNode.replaceChild(newRemoveBtn, removeBtn);
            newRemoveBtn.addEventListener('click', function() {
                card.remove();
                reindexGuests();
                updateGuestsCount();
            });
        }
    });
    guestCounter = guestCards.length;
    updateGuestsCount();
}

addGuestBtn.addEventListener('click', () => {
    const currentCount = document.querySelectorAll('#guestsContainer .guest-card').length;
    const newGuest = createGuestCard(currentCount);
    guestsContainer.appendChild(newGuest);
    reindexGuests();
});

// Инициализация удаления для существующих гостей
const removeButtons = document.querySelectorAll('.remove-guest');
removeButtons.forEach(btn => {
    btn.addEventListener('click', function() {
        const card = this.closest('.guest-card');
        card.remove();
        reindexGuests();
        updateGuestsCount();
    });
});

updateGuestsCount();

const cancelBookingBtn = document.getElementById('cancelBookingBtn');
cancelBookingBtn.addEventListener('click', async (e) =>{
    if(!await confirm('Вы точно хотите отменить бронирование?')){
        return;
    }
    const bookingStatus = document.getElementById('bookingStatus');
    const bookingId = bookingForm.dataset.bookingId;
    const response = await fetch(`/hotelsystem/admin/booking/cancel/api/v1/${bookingId}`, {
        method : "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    });
    if(response.ok){
        bookingStatus.textContent = 'Отменено';
        cancelBookingBtn.setAttribute("disabled", "");
        cancelBookingBtn.setAttribute("style", "opacity: 0.4; cursor: not-allowed;");
        await info("Бронирование отменено.");

    }else{
        await info("Что-то не так :(");
    }
});

// Обработка отправки формы
const bookingForm = document.getElementById('bookingForm');
bookingForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    if(!await confirm('Вы точно хотите изменить данные?')){
        return;
    }
    const formData = new FormData(bookingForm);
    const searchParams = new URLSearchParams(formData);
    const bookingId = bookingForm.dataset.bookingId;
    const response = await fetch(`/hotelsystem/admin/booking/guests/update/${bookingId}`, {
        method: "POST",
        headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        },
        body: searchParams,
    });
    if(response.ok){
        await info("Данные изменены.");
    }else{
        await info("Что-то не так :(");
    }
});