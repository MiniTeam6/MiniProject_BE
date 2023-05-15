# 🗓️ Panrty
### 연차/당직관리 시스템


## 🧾 주요 기능
[🔐  로그인 / 회원가입 / 유저 관리권한](#💡-user---회원관련-기능)
- JWT Authorization
- Spring Security
- 토큰 발급을 이용한 로그인 구현
- user/admin api 구분
- 회원가입시 가입 요청대기 

📇 연차 / 당직 신청

📨 연차 / 당직 승인
## 🔨 Web Architecture
## 🤹‍♂ Tech Stack
## 📜 REST API
### 💡 User - 회원관련 기능
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
|API Path|HTTP Method|기능|
|------|---|---|
|/api/admin/signup|GET|💖 회원가입 요청 리스트|
|/api/admin/status|POST|💖 회원가입 승인|
|/api/admin/role/list|GET|💖 전체유저 정보리스트|
|/api/admin/role/update|GET|💖 회원권한 업데이트|
|/api/admin/search|GET|💖 회원 검색(유저정보 출력)|

### 💡 User - 연차/당직관련 기능
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
|API Path|HTTP Method|기능|
|------|---|---|
|/api/admin/annual/order|POST|💖 연차 승인|
|/api/admin/duty/order|POST|💖 당직 승인|
|/api/admin/annual/request|GET|💖 연차신청 리스트|
|/api/admin/duty/request|GET|💖 당직신청 리스트|
|/api/admin/annual/approval|GET|💖 결재된 연차 리스트|
|/api/admin/duty/approval|GET|💖 결재된 당직 리스트|
