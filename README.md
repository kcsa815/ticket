<h2><mark>공연 예매 페이지</mark></h2>
<p>실시간 좌석 선택이 가능한 뮤지컬 예매 플랫폼입니다.<br>
사용자는 지역별/카테고리별 공연을 조회하고 예매할 수 있으며, 관리자는 공연 및 공연 회차를 시각적으로 관리할 수 있습니다.</p>
<hr>

### 프로젝트 시연 (좌석 예매 프로세스)
![리드미_시연영상_진짜최종](https://github.com/user-attachments/assets/d8e39674-c682-4b27-bfd1-f53bc09b3d4b)

<br>

### 배포 링크 (Live Demo)
<ul>
  <li>Frontend: [https://ticket-frontend-swart.vercel.app/](https://ticket-frontend-swart.vercel.app/)</li>
  <li>Backend: [https://musical-backend.onrender.com](https://musical-backend.onrender.com)</li>
  <li>Test ID: admin@test.com / admin123 (관리자 계정)</li>
</ul>

<br>

### Tech Stack

| **백엔드** | | **프론트엔드** | |
|-----------|------------|----------------|------------|
| **Framework** | <img src="https://img.shields.io/badge/springboot-6DB33F?style=flat&logo=springboot&logoColor=white"/> | **Core** | <img src="https://img.shields.io/badge/react-61DAFB?style=flat&logo=react&logoColor=white"/> <img src="https://img.shields.io/badge/typescript-3178C6?style=flat&logo=typescript&logoColor=white"/> <img src="https://img.shields.io/badge/vite-646CFF?style=flat&logo=vite&logoColor=white"/> |
| **Security** | <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=flat&logo=springsecurity&logoColor=white"/> <br> <img src="https://img.shields.io/badge/jwt-6DB33F?style=flat&logo=jsonwebtokens&logoColor=white"/> | **Styling** | <img src="https://img.shields.io/badge/cssmodules-000000?style=flat&logo=cssmodules&logoColor=white"/> |
| **Database** | <img src="https://img.shields.io/badge/mysql-4479A1?style=flat&logo=mysql&logoColor=white"/> <br> <img src="https://img.shields.io/badge/hibernate-59666C?style=flat&logo=hibernate&logoColor=white"/> | **State Management** | <img src="https://img.shields.io/badge/Context API-000000?style=flat&logo=&logoColor=white"/> |
| **Build** | <img src="https://img.shields.io/badge/gradle-02303A?style=flat&logo=gradle&logoColor=white"/> | **Libraries** | <img src="https://img.shields.io/badge/axios-000000?style=flat&logo=axios&logoColor=white"/> <br> <img src="https://img.shields.io/badge/Reactrouter-61DAFB?style=flat&logo=reactrouter&logoColor=white"/> <br> <img src="https://img.shields.io/badge/React Calendar-61DAFB?style=flat&logo=react&logoColor=white"/> <br> <img src="https://img.shields.io/badge/React Simple Maps-61DAFB?style=flat&logo=react&logoColor=white"/> <br> <img src="https://img.shields.io/badge/React Draggable-61DAFB?style=flat&logo=react&logoColor=white"/> |


<br>

#### DevOps
- **Deploy:** <img src="https://img.shields.io/badge/vercel-000000?style=flat&logo=vercel&logoColor=white"/>, <img src="https://img.shields.io/badge/render-000000?style=flat&logo=render&logoColor=white"/>
- **DB Hosting:** <img src="https://img.shields.io/badge/Aiven Cloud MySQL-000000?style=flat&logo=aiven&logoColor=white"/>
<br>

- ### 백엔드 핵심 기능

#### 1. 좌석 예매 시스템 (Interactive Booking)
- **좌표(X, Y) 기반**의 실제 공연장 좌석 배치도 구현
- `react-draggable`을 이용한 예매 팝업창 이동 기능
- 좌석 등급(VIP, R, S, A)별 색상 구분 및 실시간 잔여석 확인

#### 2. 지역별 공연 조회 (Interactive Map)
- `TopoJSON` 데이터를 활용한 대한민국 지도 시각화
- 지역 클릭 시 해당 지역(`SEOUL`, `BUSAN` 등)의 공연 목록 비동기 조회

#### 3. 관리자(Admin) 기능
- **공연장 등록:** 이미지 위에 좌석을 배치하는 것이 아닌, 템플릿 기반 자동 생성 로직 구현
- **공연 등록:** 포스터 미리보기 및 상세 정보 에디터
- **회차 관리:** 날짜 및 등급별 가격 설정 시 좌석 자동 생성

<br>

### 프런트엔드 핵심 기능

#### 1. 좌석 시스템 시각화 및 UX
<img width="600" height="auto" alt="image" src="https://github.com/user-attachments/assets/a7ef2a1d-bbcf-4f22-8338-dc04801452fb" />
<p> **절대 좌표 렌더링:** 데이터베이스의 `xCoord, yCoord` 값을 이용한 정밀한 좌석 배치</p>
<p> **모달 UX:** Draggable Modal을 활용하여 사용자가 좌석 선택 중 화면을 자유롭게 이동할 수 있도록 구현</p>

<br>

#### 2. 인터랙티브 지역별 조회
<img width="600" height="auto" alt="image" src="https://github.com/user-attachments/assets/40b6562b-328c-4212-a212-1bf7342b4372" />
<p>**GeoJSON 처리:** `react-simple-maps`를 이용해 지도 데이터를 시각화하고, 클릭한 지역(예: SEOUL)에 해당하는 데이터만 실시간으로 필터링하여 출력.</p>
<br>

#### 3. 관리자 포스터 에디터
<img width="600" height="auto" alt="image" src="https://github.com/user-attachments/assets/3de3d64c-df54-4862-bd42-fb583bc64fac" />
<p>**File I/O Abstraction:** `Blob` 객체와 `FormData`를 활용하여 복잡한 이미지 파일과 JSON 데이터를 동시에 백엔드 API로 전송하는 로직 구현.</p>
<br>

### Troubleshooting

#### 1. JPA N+1 문제 및 LazyInitializationException
- **문제:** 공연 상세 정보 조회 시, 연관된 `Venue`, `Cast` 정보를 가져올 때 N+1 쿼리가 발생하거나, 세션 종료 후 접근 에러 발생.
- **해결:** Repository에서 `@Query`와 `JOIN FETCH`를 사용하여 필요한 데이터를 한 번의 쿼리로 즉시 로딩(Eager Loading)하도록 최적화.

#### 2. 배포 환경에서의 Docker 빌드 오류
- **문제:** 로컬(Windows)에서는 잘 되던 빌드가 Render(Linux)에서 `gradlew: Permission denied` 및 `JAVA_HOME` 에러 발생.
- **해결:** 1. `git update-index --chmod=+x gradlew`로 실행 권한 부여.
  2. `Dockerfile`을 작성하여 `eclipse-temurin:17-jdk` 이미지를 기반으로 빌드 환경 통일.

#### 3. React와 Spring Boot 간의 CORS 및 데이터 매핑 이슈
- **문제:** 프론트엔드에서 보내는 JSON Key(`x`, `y`)와 백엔드 DTO(`xCoord`, `yCoord`) 불일치로 인한 400 Bad Request 발생.
- **해결:** DTO 필드명을 명확히 통일하고, Spring Security 설정에서 배포된 프론트엔드 도메인에 대한 CORS 허용 설정 추가.

  <br>

### 기능흐름도
 <div style="display:flex;">
   <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/ab5887bb-e6bf-4eba-81e9-2365515d1e90" />
   <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/b91899c3-81ec-4e0d-b2cb-2b0382eb2104" />
</div>
<div style="display:flex;">
  <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/0e90fe62-7173-49b6-aa57-cf793b9a40a5" />
  <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/3f92fd16-c18a-441c-8b0d-1a6a602365c3" />
</div>

 <br>
 
 ### ERD
<img width="1323" height="714" alt="DB_ERD" src="https://github.com/user-attachments/assets/de1f1fc3-43ac-44e2-9dcc-234557ee34ff" />

<br>

### Screen Shots
<div style="display:flex;">
  <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/db4f3028-ecd2-41cb-9fbc-a3d9830779aa" />
  <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/41dada74-28b7-46d4-a52f-42e4751bafec" />
  <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/492c3cfd-3ea7-415e-9d32-ec8f9a5b4205" />
  <img width="500" height="auto" alt="image" src="https://github.com/user-attachments/assets/206b3102-0429-4f54-b730-3a4fcf113141" />
</div>


