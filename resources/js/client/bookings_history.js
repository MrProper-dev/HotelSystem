// Модальное окно для отмены бронирования
const modalOverlay = document.getElementById('cancelModal');
const cancelBookingIdInput = document.getElementById('cancelBookingId');
const cancelRoomNameSpan = document.getElementById('cancelRoomName');
const closeModalBtn = document.getElementById('closeModalBtn');
const cancelForm = document.getElementById('cancelForm');

// Все кнопки отмены
const cancelButtons = document.querySelectorAll('.action-btn.cancel:not([disabled])');

function openModal(bookingId, roomName) {
    cancelBookingIdInput.value = bookingId;
    cancelRoomNameSpan.textContent = roomName;
    modalOverlay.classList.add('active');
}

function closeModal() {
    modalOverlay.classList.remove('active');
}

cancelButtons.forEach(btn => {
    btn.addEventListener('click', function(e) {
        e.preventDefault();
        const bookingCard = this.closest('.booking-card');
        const bookingId = this.getAttribute('data-booking-id') || bookingCard.getAttribute('data-booking-id');
        const roomNameElement = bookingCard.querySelector('.room-name');
        const roomName = roomNameElement ? roomNameElement.textContent : 'номер';
        openModal(bookingId, roomName);
    });
});

closeModalBtn.addEventListener('click', closeModal);

// Закрытие при клике на оверлей
modalOverlay.addEventListener('click', function(e) {
    if (e.target === modalOverlay) {
        closeModal();
    }
});

// Обработка формы отмены
cancelForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const bookingId = cancelBookingIdInput.value;
    
    // Имитация отправки на сервер
    alert(`Бронирование #${bookingId} успешно отменено.\n\nСервер получил запрос на отмену.`);
    closeModal();
    
    // В реальном проекте здесь был бы fetch или отправка формы
    // this.submit(); - раскомментировать для реальной отправки
});