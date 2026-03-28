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

const blockUserBtn = document.getElementById('blockUserBtn');
blockUserBtn.addEventListener('click', async (e) => {
    if(!await confirm('Вы точно хотите выполнить это действие?')){
        return;
    }
    const clientId = blockUserBtn.dataset.clientId;
    const status = document.querySelector('.status-badge');
    const response = await fetch(`/hotelsystem/admin/client/status/update/${clientId}`, {
        method: "POST"
    });
    if(response.ok){
        if(status.textContent === 'Статус: активен'){
            status.textContent = 'Статус: заблокирован';
            blockUserBtn.textContent = 'Разблокировать пользователя';
        }else{
            status.textContent = 'Статус: активен';
            blockUserBtn.textContent = 'Заблокировать пользователя';
        }
        await info("Данные изменены.");
    }else{
        await info("Что-то не так :(");
    }
});