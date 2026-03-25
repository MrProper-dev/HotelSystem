// Кнопка "Отмена" - сброс формы к исходным значениям
        const cancelBtn = document.getElementById('cancelBtn');
        const profileForm = document.getElementById('profileForm');
        
        cancelBtn.addEventListener('click', function() {
            if (confirm('Отменить все изменения? Несохраненные данные будут потеряны.')) {
                profileForm.reset();
                // Восстанавливаем исходные значения
                document.querySelector('input[name="email"]').value = 'ivan.ivanov@example.com';
                document.querySelector('input[name="password"]').value = 'password123';
                document.querySelector('input[name="name"]').value = 'Иван';
                document.querySelector('input[name="phone"]').value = '+7 (900) 123-45-67';
                document.querySelector('input[name="surname"]').value = 'Иванов';
                document.querySelector('input[name="firstname"]').value = 'Иван';
                document.querySelector('input[name="patronymic"]').value = 'Иванович';
                document.querySelector('input[name="birthdate"]').value = '1990-01-01';
                document.querySelector('input[name="doc_series"]').value = '1234';
                document.querySelector('input[name="doc_number"]').value = '567890';
                document.querySelector('input[name="department_code"]').value = '770-001';
                document.querySelector('input[name="issue_date"]').value = '2010-03-15';
                alert('Изменения отменены');
            }
        });

        // Обработка отправки формы
        profileForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(profileForm);
            let dataString = '';
            for (let [key, value] of formData.entries()) {
                if (key === 'password' && value === 'password123') {
                    dataString += `${key}: [не изменен]\n`;
                } else {
                    dataString += `${key}: ${value || '[пусто]'}\n`;
                }
            }
            
            alert(`✅ Данные успешно обновлены!\n\nСервер получил:\n${dataString}\n\nИзменения сохранены.`);
            
            // В реальном проекте здесь был бы fetch или отправка формы
            // this.submit(); - раскомментировать для реальной отправки
        });