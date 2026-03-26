// Динамический расчет стоимости и количества ночей
const pricePerNight = document.getElementById('pricePerNight').getAttribute("price");
const checkinInput = document.getElementById('checkin');
const checkoutInput = document.getElementById('checkout');
const nightsSummary = document.getElementById('nightsSummary');
const totalPriceSpan = document.getElementById('totalPrice');
const roomId = document.getElementById('roomId');

const infoModal    = document.getElementById("infoModal");
const infoModalOk  = document.getElementById("infoModalOk");
const infoModalText = document.getElementById("infoModalText");

const confirmModal     = document.getElementById("confirmModal");
const confirmModalOk   = document.getElementById("confirmModalOk");
const confirmModalCancel = document.getElementById("confirmModalCancel");
const confirmModalText  = document.getElementById("confirmModalText");


function calculateNights() {
    const checkin = new Date(checkinInput.value);
    const checkout = new Date(checkoutInput.value);
    
    if (checkin && checkout && checkout > checkin) {
        const diffTime = Math.abs(checkout - checkin);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        return diffDays;
    }
    return 0;
}

function updateSummary() {
    const nights = calculateNights();
    const total = nights * pricePerNight;
    
    const nightsText = nights === 1 ? '1 ночь' : (nights >= 2 && nights <= 4 ? `${nights} ночи` : `${nights} ночей`);
    nightsSummary.textContent = `${nightsText}`;
    totalPriceSpan.textContent = total.toLocaleString('ru-RU');
}

checkinInput.addEventListener('change', updateSummary);
checkoutInput.addEventListener('change', updateSummary);
updateSummary();

// Динамическое добавление гостей
let guestCount = 1;
const guestsContainer = document.getElementById('guestsContainer');
const addGuestBtn = document.getElementById('addGuestBtn');

function createGuestCard(index) {
    const guestCard = document.createElement('div');
    guestCard.className = 'guest-card';
    guestCard.setAttribute('data-guest-id', index + 1);
    
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
                <input type="text" class="field-input" name="guests[${index}][patronymic]" placeholder="Иванович">
            </div>
            <div class="field-group">
                <div class="field-label">Дата рождения</div>
                <input type="date" class="field-input" name="guests[${index}][birthdate]" required>
            </div>
            <div class="field-group">
                <div class="field-label">Серия пасп. / свид. о рожд.</div>
                <input type="text" class="field-input" name="guests[${index}][doc_series]" placeholder="1234 / I-АЮ" required>
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
    });
    
    return guestCard;
}

function reindexGuests() {
    const guestCards = document.querySelectorAll('#guestsContainer .guest-card');
    guestCards.forEach((card, idx) => {
        const newIndex = idx;
        const titleDiv = card.querySelector('.guest-title');
        titleDiv.textContent = `Гость ${newIndex + 1}`;
        card.setAttribute('data-guest-id', newIndex + 1);
        
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
        }
    });
    guestCount = guestCards.length;
}

addGuestBtn.addEventListener('click', () => {
    const currentCount = document.querySelectorAll('#guestsContainer .guest-card').length;
    const newGuest = createGuestCard(currentCount);
    guestsContainer.appendChild(newGuest);
});

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

// Удаление гостя
const firstRemoveBtn = document.querySelector('#guestsContainer .guest-card .remove-guest');
if (firstRemoveBtn) {
    firstRemoveBtn.addEventListener('click', function() {
        const guestCards = document.querySelectorAll('#guestsContainer .guest-card');
        if (guestCards.length > 1) {
            this.closest('.guest-card').remove();
            reindexGuests();
        } else {
            alert('Должен быть хотя бы один гость');
        }
    });
}

// Обработка отправки формы
const bookingForm = document.getElementById('bookingForm');
bookingForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const nights = calculateNights();
    if (nights <= 0) {
        const information = 'Пожалуйста, укажите корректные даты проживания';
        await info(information);
        return;
    }

    const id = roomId.value;
    const formData = new URLSearchParams();
    formData.append("check_in", checkinInput.value);
    formData.append("check_out", checkoutInput.value);
    const answer = await fetch(`/hotelsystem/booking/check/api/v1/${id}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    });

    if(!answer.ok){
        const information = 'Что-то не так, попробуйе позже.'
        await info(information);
        return;
    }

    const json = await answer.json();

    if(json.availability !== "true"){
        const information = 'Выбранные даты недоступны.'
        await info(information);
        return;
    }
    
    
    const information = 'Перед отправкой убедитесь, что все данные заполнены корректно.';
    if(!await confirm(information)){
        return;
    }else{
        bookingForm.submit();  
    }   
});