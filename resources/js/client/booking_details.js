const confirmModal     = document.getElementById('confirmModal');
const confirmModalOk   = document.getElementById('confirmModalOk');
const confirmModalCancel = document.getElementById('confirmModalCancel');
const confirmModalText  = document.getElementById('confirmModalText');

function confirm(question){
    return new Promise((resolve) => {
        confirmModalText.textContent = question;
        confirmModal.showModal();

        const onConfirm = () => {
            confirmModal.close();
            confirmModalOk.removeEventListener('click', onConfirm);
            confirmModalCancel.removeEventListener('click', onCancel);
            resolve(true);
        };

        const onCancel = () => {
            confirmModal.close();
            confirmModalOk.removeEventListener('click', onConfirm);
            confirmModalCancel.removeEventListener('click', onCancel);
            resolve(false);
        };

        confirmModalOk.addEventListener('click', onConfirm);
        confirmModalCancel.addEventListener('click', onCancel);
    });
}

const form = document.querySelector('.cancel-form');
form.addEventListener('submit', async (e) =>{
    e.preventDefault();

    const button = document.getElementById('cancelBookingBtn');
    const status = document.querySelector('.status-label');
    const bookingId = button.dataset.bookingId;

    const information = 'Вы действительно хотите отменить бронирование? Это действие нельзя будет отменить.';
    if(await confirm(information)){
        button.setAttribute("disabled", "");
        button.setAttribute("style", "opacity: 0.4; cursor: not-allowed;");
        status.className = 'booking-status cancelled';
        status.textContent = 'Отменено';
        await fetch(`/hotelsystem/booking/cancel/api/v1/${bookingId}`, {
            method : "POST",
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
    }else{
        return;
    }
});