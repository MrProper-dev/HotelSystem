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

const blockBtns = document.querySelectorAll('.blockBtn');
blockBtns.forEach((blockBtn) => {
    blockBtn.addEventListener('click', async (e) =>{
        if(!await confirm('Вы точно хотите выполнить это действие?')){
            return;
        }
        const clientId = blockBtn.dataset.clientId;
        const status = blockBtn.closest("tr").querySelector("span.client-status");
        const response = await fetch(`/hotelsystem/admin/client/status/update/${clientId}`, {
            method: "POST"
        });
        if(response.ok){
            if(status.textContent === 'активен'){
                status.textContent = 'заблокирован';
                status.classList.add('block');
                blockBtn.textContent = 'Разблокировать';
                blockBtn.classList.remove('block');
            }else{
                status.textContent = 'активен';
                status.classList.remove('blocked');
                blockBtn.textContent = 'Заблокировать';
                blockBtn.classList.add('block');
            }
            await info("Данные изменены.");
        }else{
            await info("Что-то не так :(");
        }
    });
});