# 🗓️ Panrty
### [연차/당직 관리 시스템](https://github.com/MiniTeam6/MiniProject_BE)

## 목차
* [🧾 주요 기능](#주요-기능)<br>
* [🔨 Web Architecture](#Web-Architecture)<br>
* [🤹‍♂ Tech Stack](#🤹‍Tech-Stack)<br>
* [📜 API](#API)<br>
* [프로젝트 구조](#프로젝트-구조)<br>
* [개선해야할 점](#개선해야할-점)<br>
* [앞으로의 계획](#앞으로의-계획)<br>

### 연차/당직관리 시스템

## 주요 기능
🔐  로그인 / 회원가입 / 유저 관리권한
- JWT 토큰 발급을 이용한 로그인 구현
- user/admin api 구분
- 회원가입시 가입 요청대기 

📇 연차 / 당직 신청
- 날짜 선택 및 연차/당직 신청
- 연차 보유개수보다 신청 연차일이 더 많으면 신청불가

📨 연차 / 당직 승인
- Admin결재 -> 승인/반려 
- 승인시 연차 보유개수 다시 한 번 검증, 문제 없다면 신청일 만큼 보유개수에서 차감
- 승인 리스트 검색 가능

🗓️ 모든 유저 연차/당직 월별 조회


## Web Architecture
## Tech Stack
## API
### 💡 User - 회원관련 기능
### [요청/응답 데이터](https://github.com/MiniTeam6/MiniProject_BE/wiki/%F0%9F%91%A9%F0%9F%8F%BB%E2%80%8D%F0%9F%92%BB-%EC%9A%94%EC%B2%AD-%EC%9D%91%EB%8B%B5-%EB%8D%B0%EC%9D%B4%ED%84%B0(%ED%9A%8C%EC%9B%90%EA%B4%80%EB%A0%A8-%EA%B8%B0%EB%8A%A5))
|API Path|HTTP Method|기능|
|------|---|---|
|/api/signup|POST|🌟 회원가입|
|/api/login|POST|🌟 로그인|
|/api/email|GET|🌟 이메일 중복체크|
|/api/user/users|GET|🌟 유저 리스트|
|/api/user/users/{id}|GET|🌟 유저 상세정보|
|/api/user/myinfo|GET|🌟 마이페이지|
|/api/user/myinfo|POST|🌟 내 정보 수정|

### 💡 Admin - 회원관련 기능
### [요청/응답 데이터](https://github.com/MiniTeam6/MiniProject_BE/wiki/%F0%9F%91%A9%F0%9F%8F%BB%E2%80%8D%F0%9F%92%BB-%EC%9A%94%EC%B2%AD-%EC%9D%91%EB%8B%B5%EB%8D%B0%EC%9D%B4%ED%84%B0(%ED%9A%8C%EC%9B%90-%EA%B4%80%EB%A0%A8%EA%B8%B0%EB%8A%A5)ADMIN)
|API Path|HTTP Method|기능|
|------|---|---|
|/api/admin/signup|GET|💖 회원가입 요청 리스트|
|/api/admin/status|POST|💖 회원가입 승인|
|/api/admin/role/list|GET|💖 전체유저 정보리스트|
|/api/admin/role/update|GET|💖 회원권한 업데이트|
|/api/admin/search|GET|💖 회원 검색(유저정보 출력)|

### 💡 User - 연차/당직관련 기능
### [요청/응답 데이터](https://github.com/MiniTeam6/MiniProject_BE/wiki/%F0%9F%91%A9%F0%9F%8F%BB%E2%80%8D%F0%9F%92%BB-%EC%9A%94%EC%B2%AD-%EC%9D%91%EB%8B%B5%EB%8D%B0%EC%9D%B4%ED%84%B0-(%EC%97%B0%EC%B0%A8-%EB%8B%B9%EC%A7%81-%EA%B4%80%EB%A0%A8-%EA%B8%B0%EB%8A%A5))
|API Path|HTTP Method|기능|
|------|---|---|
|/api/user/myannual|GET|🌟 내 연차 리스트|
|/api/user/myduty|GET|🌟 내 당직 리스트|
|/api/user/nextevent|GET|🌟 가장 빠른 연차당직 (D-day계산)|
|/api/user/event/add|POST|🌟 연차/당직 신청|
|/api/user/event/cancel|POST|🌟 연차/당직 신청취소(결재 전에만 가능)|
|/api/user/event/modify|POST|🌟 연차/당직 신청수정(결재 전에만 가능)|
|/api/user/event/list|GET|🌟 모든 유저 연차/당직 리스트(월별조회)|


### 💡 Admin - 연차/당직관련 기능
### [요청/응답 데이터](https://github.com/MiniTeam6/MiniProject_BE/wiki/%F0%9F%91%A9%F0%9F%8F%BB%E2%80%8D%F0%9F%92%BB-%EC%9A%94%EC%B2%AD-%EC%9D%91%EB%8B%B5%EB%8D%B0%EC%9D%B4%ED%84%B0(%EC%97%B0%EC%B0%A8-%EB%8B%B9%EC%A7%81%EA%B4%80%EB%A0%A8%EA%B8%B0%EB%8A%A5)_ADMIN)
|API Path|HTTP Method|기능|
|------|---|---|
|/api/admin/annual/order|POST|💖 연차 승인|
|/api/admin/duty/order|POST|💖 당직 승인|
|/api/admin/annual/request|GET|💖 연차신청 리스트|
|/api/admin/duty/request|GET|💖 당직신청 리스트|
|/api/admin/annual/approval|GET|💖 결재된 연차 리스트|
|/api/admin/duty/approval|GET|💖 결재된 당직 리스트|
<<<<<<< HEAD

## 프로젝트 구조
## 개선해야할 점
## 앞으로의 계획
=======
>>>>>>> 5def761 (csrf, cors 재설정)
