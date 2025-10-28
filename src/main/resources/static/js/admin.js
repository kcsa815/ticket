// admin.js

// DOM이 완전히 로드된 후에 스크립트 실행
document.addEventListener('DOMContentLoaded', () => {

    // '공연 등록' 폼이 현재 페이지에 있는지 확인
    const registerForm = document.getElementById('register-form');

    if (registerForm) {
        // 폼 제출(submit) 이벤트 리스너 추가
        registerForm.addEventListener('submit', async (event) => {
            // 폼의 기본 제출 동작(새로고침)을 막음
            event.preventDefault();

            // 1. 폼 데이터 수집
            // FormData 객체를 사용하면 폼의 name 속성을 기반으로 데이터를 쉽게 수집 가능
            const formData = new FormData(registerForm);

            // 2. 수집한 데이터를 JavaScript 객체로 변환
            const showData = {
                title: formData.get('title'),
                posterUrl: formData.get('poster'),
                startDate: formData.get('startDate'),
                endDate: formData.get('endDate'),
                description: formData.get('description'),
                seats: {
                    vip: {
                        price: parseInt(formData.get('vipPrice'), 10),
                        total: parseInt(formData.get('vipSeats'), 10)
                    },
                    r: {
                        price: parseInt(formData.get('rPrice'), 10),
                        total: parseInt(formData.get('rSeats'), 10)
                    },
                    s: {
                        price: parseInt(formData.get('sPrice'), 10),
                        total: parseInt(formData.get('sSeats'), 10)
                    },
                    a: {
                        price: parseInt(formData.get('aPrice'), 10),
                        total: parseInt(formData.get('aSeats'), 10)
                    }
                }
            };

            //fetch API를 사용하여 서버로 데이터 전송
            try{
                const response = await fetch('/admin/register', {
                    method:'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(showData) 
                });
                if (response.ok) { //서버 응답이 성공이면
                    const resultText = await response.text();
                    alert(resultText);
                    registerForm.reset(); //폼 초기화
                    window.location.href = '/admin/list'; //공연 등록 성공 시 목록 페이지로 이동

                } else{
                    alert('공연 등록에 실패했습니다. 서버 로그를 확인하세요.');
                }
            } catch(error) {
                console.error('공연 등록 중 오류 발생:', error);
                alert('공연 등록 중 오류가 발생했습니다.');
            }

            
        });
    }

    const deleteButtons = document.querySelectorAll('.delete-btn');
    
    deleteButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            event.preventDefault(); 

            const confirmDelete = confirm('정말로 이 공연을 삭제하시겠습니까?');
            
            if (confirmDelete) {
                const showId = button.dataset.showId;
                
                try {
                    const response = await fetch(`/admin/delete/${showId}`, {
                        method: 'DELETE' // 💡 'DELETE' 메서드 사용
                    });

                    if (response.ok) {
                        const resultText = await response.text();
                        alert(resultText); // (예: "공연이 삭제되었습니다.")
                        
                        const row = button.closest('tr');
                        row.remove();

                    } else {
                        alert('삭제에 실패했습니다. 서버 로그를 확인하세요.');
                    }
                } catch (error) {
                    console.error('삭제 중 오류 발생:', error);
                    alert('삭제 중 오류가 발생했습니다.');
                }
            }
        });
    });
});