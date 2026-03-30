const infoModal    = document.getElementById("infoModal");
const infoModalOk  = document.getElementById("infoModalOk");
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

const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(loginForm);
    const login = formData.get('login');
    const password = formData.get('password');

    const form = new URLSearchParams();
    form.append("login", login);
    form.append("password", password);
    
    const answer = await fetch('/hotelsystem/super/login/api/v1', {
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

    if(json.isValid !== "true"){
        const information = 'Неправильный логин или пароль.';
        await info(information);
        return;
    }

    loginForm.submit();
});