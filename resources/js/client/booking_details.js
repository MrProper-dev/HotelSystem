// Модальное окно для отмены бронирования
const modalOverlay = document.getElementById('cancelModal');
const cancelBookingBtn = document.getElementById('cancelBookingBtn');
const closeModalBtn = document.getElementById('closeModalBtn');
const cancelForm = document.getElementById('cancelForm');

if (cancelBookingBtn) {
    cancelBookingBtn.addEventListener('click', function() {
        modalOverlay.classList.add('active');
    });
}

closeModalBtn.addEventListener('click', function() {
    modalOverlay.classList.remove('active');
});

// Закрытие при клике на оверлей
modalOverlay.addEventListener('click', function(e) {
    if (e.target === modalOverlay) {
        modalOverlay.classList.remove('active');
    }
});

// Обработка формы отмены
cancelForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const formData = new FormData(cancelForm);
    let dataString = '';
    for (let [key, value] of formData.entries()) {
        dataString += `${key}: ${value}\n`;
    }
    alert(`✅ Бронирование успешно отменено!\n\nСервер получил данные:\n${dataString}`);
    modalOverlay.classList.remove('active');
    
    // В реальном проекте здесь был бы fetch или отправка формы
    // this.submit(); - раскомментировать для реальной отправки
});