const cancelBtn = document.getElementById('cancelBtn');
const profileForm = document.getElementById('profileForm');
const logoutBtn = document.getElementById('logoutBtn');

const infoModal    = document.getElementById("infoModal");
const infoModalOk  = document.getElementById("infoModalOk");
const infoModalText = document.getElementById("infoModalText");

const confirmModal     = document.getElementById("confirmModal");
const confirmModalOk   = document.getElementById("confirmModalOk");
const confirmModalCancel = document.getElementById("confirmModalCancel");
const confirmModalText  = document.getElementById("confirmModalText");

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

cancelBtn.addEventListener('click', function() {
    profileForm.reset();
});

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

logoutBtn.addEventListener('click', async (e) => {
    e.preventDefault();

    const information = 'Вы действительно хотите выйти?';
    if(await confirm(information)){
        window.location.href = '/hotelsystem/logout';
    }
});

profileForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.querySelector('input[name="email"]').value;
    const password = document.querySelector('input[name="password"]').value;
    const name = document.querySelector('input[name="name"]').value;
    const phone = document.querySelector('input[name="phone"]').value;

    const information = 'Данные изменены.';
    await info(information);
    
    const formData = new URLSearchParams();
    formData.append('email', email);
    formData.append('password', password);
    formData.append('name', name);
    formData.append('phone', phone);

    await fetch(`/hotelsystem/client/update/api/v1`, {
        method : "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: formData.toString()
    });
});