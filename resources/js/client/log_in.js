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

// Обработка отправки формы
const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(loginForm);
    const email = formData.get('email');
    const password = formData.get('password');


    const form = new URLSearchParams();
    form.append("email", email);
    form.append("password", password);
    
    const answer = await fetch('/hotelsystem/login/api/v1', {
        method : "POST",
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: form.toString()
    });
    
    if(!answer.ok){
        const information = 'Что-то не так, попробуйе позже.'
        await info(information);
        return;
    }

    const json = await answer.json();

    if(json.wrongPassword === "true"){
        const information = 'Неправильный логин или пароль.';
        await info(information);
        return;
    }

    loginForm.submit();
});