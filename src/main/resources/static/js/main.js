// DOM이 모두 로드된 후에 Swiper를 초기화합니다.
document.addEventListener('DOMContentLoaded', function () {

    // 우리가 HTML에서 .main-banner-swiper 라고 이름 붙인 클래스를 선택합니다.
    const mainBannerSwiper = new Swiper('.main-banner-swiper', {
        // 옵션 설정
        
        loop: true, // true: 무한 반복
        autoplay: {
            delay: 5000, // 5초마다 자동으로 넘어감
            disableOnInteraction: false, // 사용자가 조작한 후에도 자동 재생 계속
        },
        
        // Pagination (페이지 번호 점)
        pagination: {
            el: '.swiper-pagination',
            clickable: true, // 점을 클릭해서 슬라이드 이동 가능
        },
        
        // Navigation arrows (좌우 화살표)
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    });

});