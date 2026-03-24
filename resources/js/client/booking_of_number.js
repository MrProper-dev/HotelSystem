// Динамический расчет стоимости и количества ночей
        const pricePerNight = 4200;
        const checkinInput = document.getElementById('checkin');
        const checkoutInput = document.getElementById('checkout');
        const nightsSummary = document.getElementById('nightsSummary');
        const totalPriceSpan = document.getElementById('totalPrice');

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
                        <input type="text" class="field-input" name="guests[${index}][surname]" placeholder="Иванов" required>
                    </div>
                    <div class="field-group">
                        <div class="field-label">Имя</div>
                        <input type="text" class="field-input" name="guests[${index}][name]" placeholder="Иван" required>
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
                        <input type="text" class="field-input" name="guests[${index}][passport_series]" placeholder="1234 / I-АЮ" required>
                    </div>
                    <div class="field-group">
                        <div class="field-label">Номер пасп. / свид. о рожд.</div>
                        <input type="text" class="field-input" name="guests[${index}][passport_number]" placeholder="567890" required>
                    </div>
                </div>
            `;
            
            // Добавляем обработчик для кнопки удаления
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
                
                // Переименовываем все поля ввода
                const inputs = card.querySelectorAll('input');
                inputs.forEach(input => {
                    const name = input.getAttribute('name');
                    if (name) {
                        const newName = name.replace(/guests\[\d+\]/, `guests[${newIndex}]`);
                        input.setAttribute('name', newName);
                    }
                });
                
                // Обновляем кнопку удаления
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

        // Удаление гостя (для первого гостя тоже можно добавить, но для первого сделаем отдельно)
        // Добавляем возможность удалить и первого гостя, если нужно
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
        // const bookingForm = document.getElementById('bookingForm');
        // bookingForm.addEventListener('submit', function(e) {
        //     e.preventDefault();
            
        //     const nights = calculateNights();
        //     if (nights <= 0) {
        //         alert('Пожалуйста, укажите корректные даты проживания');
        //         return;
        //     }
            
        //     const total = nights * pricePerNight;
            
        //     // Собираем данные для отправки
        //     const formData = new FormData(bookingForm);
        //     formData.append('total_nights', nights);
        //     formData.append('total_price', total);
            
        //     // Имитация отправки на сервер
        //     alert(`Бронирование успешно создано!\n\nКомната: Стандарт "Лайт"\nКоличество ночей: ${nights}\nИтоговая сумма: ${total.toLocaleString('ru-RU')} ₽\n\nДанные отправлены на сервер.`);
            
        //     // Здесь можно раскомментировать для реальной отправки:
        //     // fetch('/api/bookings', {
        //     //     method: 'POST',
        //     //     body: formData
        //     // }).then(response => response.json())
        //     //   .then(data => console.log(data));
        // });