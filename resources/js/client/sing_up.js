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

// Обработка отправки формы с проверкой паролей
const registerForm = document.getElementById('registerForm');
registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = new FormData(registerForm);
    const name = formData.get('name');
    const phone = formData.get('phone');
    const email = formData.get('email');
    const password = formData.get('password');
    const passwordConfirm = formData.get('password_confirm');
    
    // Проверка совпадения паролей
    if (password !== passwordConfirm) {
        const information = 'Ошибка: Пароли не совпадают!\n\nПожалуйста, введите одинаковые пароли.';
        await info(information);
        return;
    }
    
    // Проверка минимальной длины пароля
    if (password.length < 6) {
        const information = 'Ошибка: Пароль должен содержать минимум 6 символов!';
        await info(information);
        return;
    }

    const form = new URLSearchParams();
    form.append("email", email);
    
    const answer = await fetch('/hotelsystem/singup/api/v1', {
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

    if(json.exist === "true"){
        const information = 'Вы уже зарегистрированы.'
        await info(information);
        return;
    }

    registerForm.submit();
});