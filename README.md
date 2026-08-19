# ✈️ MCM Boarding Pass

### 온라인의 취향이 오프라인의 여정이 되는, MCM HAUS 브랜드 경험 플랫폼

자사몰에서 담은 위시리스트가 한 장의 보딩패스가 되고,  

매장에서 스캔하는 순간 나만을 위한 관람 동선이 펼쳐집니다.  

비행이 끝나면 그 기록은 디지털 여권에 스탬프로 남습니다.

  


Java
Spring Boot
PostgreSQL
Redis
Azure
Docker
OpenAI

  


**한성대학교 멋쟁이사자처럼 14기 해커톤** · 팀 `감자탕에감자없음`



  


## 목차

- [기획 배경](#기획-배경)
- [핵심 경험](#핵심-경험)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [ERD](#erd)
- [API 명세](#api-명세)
- [설계 포인트](#설계-포인트)
- [로컬 실행](#로컬-실행)
- [배포](#배포)
- [팀](#팀)



## 기획 배경

명품 매장은 여전히 문턱이 높습니다.

Z세대와 럭셔리 입문자는 직원 응대와 무거운 분위기에 부담을 느껴 매장 방문 자체를 망설이고,
브랜드를 오래 사랑해온 코어 고객은 단발성 구매에 그쳐 브랜드와의 연결이 이어지지 않습니다.

**MCM Boarding Pass**는 이 두 간극을 *여행*이라는 서사로 잇습니다.


|                    | Pain Point                  | Solution                 |
| ------------------ | --------------------------- | ------------------------ |
| **Z세대 · 럭셔리 입문자**  | 매장 방문 시 응대 부담, 자유로운 관람이 어려움 | 보딩패스 스캔만으로 시작되는 자율 관람 동선 |
| **MCM 매니아 (4050)** | 구매 이후 브랜드와의 접점 단절           | 방문·관람 이력이 쌓이는 디지털 여권     |




## 핵심 경험

```
   구매 전 · 온라인              구매 중 · 오프라인            구매 후 · 리텐션
 ─────────────────────      ─────────────────────      ─────────────────────
  위시리스트 · 쇼핑백    →      보딩패스 QR 스캔      →        MCM Passport 기록
  취향 설문 6문항              AI 맞춤 관람 동선              방문 스탬프 적립
  Boarding Pass 발급         음성 도슨트 해설               크레딧으로 AI 피팅
                                    │                          │
                                    └────────  재방문  ◀─────────┘
```

온라인 행동 데이터가 오프라인 방문을 만들고,
오프라인 방문이 다시 온라인 기능(AI 피팅 크레딧)을 여는 **순환 구조**입니다.

## 주요 기능



### 🎫 Boarding Pass

발급 시점의 위시리스트·쇼핑백을 **스냅샷으로 고정**해 티켓에 수록합니다.
이후 찜을 취소해도 이미 발급된 티켓의 내용은 변하지 않습니다.

온라인 이용 기록이 없거나 데이터 활용에 동의하지 않은 경우,
설문 응답만으로 상품을 선정하는 **폴백 경로**가 동작합니다.

```
ISSUED ──scan──▶ SCANNED ──complete──▶ COMPLETED
   │                 │                      │
 티켓 발급      방문 기록 생성          체류 시간 계산
 동선 계산      크레딧 700 적립          여권 스탬프 발급
```



### 🤖 AI 관람 동선 추천

취향 설문 6문항과 위시리스트를 근거로, MCM HAUS 4개 층 중
**지금 이 관람객에게 맞는 층**을 골라 추천 사유와 함께 제시합니다.

전시는 층별 서사가 있으므로 **모든 층을 순서대로 반환**하되,
추천 층만 `isRecommended: true`로 강조하는 방식을 택했습니다.


| 단계  | 기준                                              |
| --- | ----------------------------------------------- |
| 1순위 | Q2 관심사 태그 → 동일 코드의 층                            |
| 2순위 | Q3 스타일 태그 → 매핑된 층                               |
| 컷오프 | Q4 관람 시간 (`QUICK` 2 / `STANDARD` 3 / `FULL` 전체) |


추천 사유는 **GPT가 설문·상품 맥락을 반영해 한 문장으로 생성**합니다.

### 🔊 음성 도슨트

비행 인트로와 층별 해설을 사전 생성한 음원으로 제공합니다.
인트로는 기내방송 음역대(300–3400Hz) 필터를 적용해 실제 안내방송처럼 들리도록 했습니다.

### 👗 AI 가상 피팅

업로드한 전신 사진에 상품을 착용시킨 이미지를 생성합니다.
매장 방문으로 얻은 크레딧을 소모하며, 생성 실패 시 자동 환급됩니다.

### 🛂 MCM Passport

여권 신분면 · 방문 스탬프 · 관람 이력 · 크레딧 내역을 제공합니다.
크레딧은 잔액만 저장하지 않고 **원장(ledger)** 으로 모든 증감을 기록해,
"왜 지금 이 잔액인지"를 추적할 수 있습니다.

## 기술 스택


| 구분            | 사용 기술                                               |
| ------------- | --------------------------------------------------- |
| **Language**  | Java 21                                             |
| **Framework** | Spring Boot 3.5.9, Spring Data JPA, Spring Security |
| **Database**  | PostgreSQL 15, Redis 7                              |
| **Auth**      | JWT, Kakao OAuth 2.0                                |
| **AI**        | OpenAI API (동선 추천 사유 생성, 가상 피팅 이미지 생성)              |
| **Storage**   | Azure Blob Storage                                  |
| **Infra**     | Azure VM, Azure Database for PostgreSQL, Docker     |
| **CI/CD**     | GitHub Actions, GHCR                                |
| **Docs**      | Swagger (springdoc-openapi)                         |




## 시스템 아키텍처

```
                            ┌──────────────┐
                            │    Client    │
                            └──────┬───────┘
                                   │ HTTPS
                    ┌──────────────▼───────────────┐
                    │        Azure VM (Docker)     │
                    │  ┌────────────────────────┐  │
                    │  │   Spring Boot 3.5      │  │
                    │  │   JWT · Security       │  │
                    │  └───┬────────┬───────┬───┘  │
                    └──────┼────────┼───────┼──────┘
                           │        │       │
            ┌──────────────┘        │       └──────────────┐
            ▼                       ▼                      ▼
  ┌───────────────────┐   ┌──────────────────┐   ┌──────────────────┐
  │ Azure PostgreSQL  │   │ Azure Blob       │   │ OpenAI API       │
  │ Flexible Server   │   │ 이미지 · 음원       │   │ 동선 사유 · 피팅    │
  └───────────────────┘   └──────────────────┘   └──────────────────┘

  ┌─────────────────────────────────────────────────────────────────┐
  │  GitHub Actions                                                 │
  │  PR → dev  :  빌드 · 테스트 (PostgreSQL · Redis 서비스 컨테이너)       │
  │  push main :  Docker 빌드 → GHCR 푸시 → VM SSH 배포 → Discord      │
  └─────────────────────────────────────────────────────────────────┘
```



## ERD

21개 테이블, 4개 도메인으로 구성됩니다.


| 도메인         | 테이블                                                                                                        |
| ----------- | ---------------------------------------------------------------------------------------------------------- |
| **회원 · 여권** | `users` `passport` `passport_stamp` `credit_ledger`                                                        |
| **상품**      | `product` `product_color` `product_image` `product_size` `wishlist` `shopping_bag_item` `fitting_session`  |
| **여정**      | `boarding_pass` `boarding_pass_item` `boarding_pass_survey` `route_step` `survey_question` `survey_option` |
| **공간**      | `store` `visit_log` `floor` `floor_content`                                                                |


**설계 시 고려한 지점**

**스냅샷 분리** — `boarding_pass_item`은 위시리스트를 조인하지 않고 발급 시점 데이터를 복사합니다. 이후 유저가 찜을 취소해도 발급된 티켓은 그대로 유지됩니다.

**원장 구조** — `credit_ledger`는 모든 크레딧 증감을 행으로 기록하고, `passport.credit_balance`는 조회 성능을 위한 캐시 컬럼입니다. 두 값은 항상 같은 트랜잭션에서 갱신됩니다.

**3단 상품 구조** — `product → product_color → product_size`. 위시리스트는 색상 단위, 쇼핑백은 사이즈 단위로 참조해 커머스 도메인의 정밀도 차이를 반영했습니다.

**콘텐츠 블록** — `floor_content`는 `block_type`(TEXT / QUOTE / IMAGE / PRODUCT / LIST)으로 구분해, 층마다 다른 구성의 전시 콘텐츠를 하나의 스키마로 처리합니다.

**중복 방지 제약** — 티켓 재스캔으로 인한 크레딧 중복 지급을 막기 위해 `visit_log.boarding_pass_id`에 UNIQUE를 걸었습니다. 애플리케이션 검증만으로는 동시 요청을 막을 수 없기 때문입니다.

## API 명세

전체 **26개 엔드포인트**. 모든 응답은 공통 래퍼로 감싸집니다.

```json
{
  "isSuccess": true,
  "code": "COMMON2000",
  "message": "성공입니다.",
  "result": { }
}
```

**엔드포인트 전체 보기**

### Auth · User


| Method   | Endpoint               | 설명               |
| -------- | ---------------------- | ---------------- |
| `POST`   | `/auth/kakao`          | 카카오 로그인          |
| `POST`   | `/auth/signup`         | 일반 회원가입          |
| `POST`   | `/auth/login`          | 일반 로그인           |
| `POST`   | `/auth/profile`        | 추가 정보 입력 · 여권 발급 |
| `GET`    | `/users/me`            | 내 정보 조회          |
| `PATCH`  | `/users/me`            | 회원정보 수정          |
| `PUT`    | `/users/me/body-image` | 바디 이미지 등록        |
| `DELETE` | `/users/me/body-image` | 바디 이미지 삭제        |




### Product · Wishlist · ShoppingBag


| Method   | Endpoint                            | 설명        |
| -------- | ----------------------------------- | --------- |
| `GET`    | `/products`                         | 상품 목록 조회  |
| `GET`    | `/products/{productId}`             | 상품 상세 조회  |
| `POST`   | `/wishlist`                         | 위시리스트 담기  |
| `GET`    | `/wishlist`                         | 위시리스트 조회  |
| `DELETE` | `/wishlist/{productColorId}`        | 위시리스트 삭제  |
| `POST`   | `/shopping-bag`                     | 쇼핑백 담기    |
| `GET`    | `/shopping-bag`                     | 쇼핑백 조회    |
| `DELETE` | `/shopping-bag/{shoppingBagItemId}` | 쇼핑백 항목 삭제 |




### Boarding Pass · Survey · Floor


| Method | Endpoint                         | 설명               |
| ------ | -------------------------------- | ---------------- |
| `GET`  | `/surveys/questions`             | 취향 설문 문항 조회      |
| `POST` | `/boarding-passes`               | 보딩패스 발급 · 동선 계산  |
| `GET`  | `/boarding-passes/latest`        | 최근 보딩패스 조회       |
| `POST` | `/boarding-passes/{id}/scan`     | 매장 스캔 · 크레딧 적립   |
| `GET`  | `/boarding-passes/{id}/route`    | AI 추천 동선 조회      |
| `POST` | `/boarding-passes/{id}/complete` | 비행 종료 · 스탬프 발급   |
| `GET`  | `/floors`                        | 층 목록 조회          |
| `GET`  | `/floors/{floorId}`              | 층 상세 · 전시 콘텐츠 조회 |




### Passport · Fitting


| Method | Endpoint                        | 설명            |
| ------ | ------------------------------- | ------------- |
| `GET`  | `/passport`                     | 여권 신분면 조회     |
| `GET`  | `/passport/stamps`              | 방문 스탬프 목록     |
| `GET`  | `/passport/visits/{visitLogId}` | 방문 상세 · 관람 이력 |
| `GET`  | `/passport/credits`             | 크레딧 내역        |
| `POST` | `/fitting-sessions/upload-url`  | 업로드 URL 발급    |
| `POST` | `/fitting-sessions`             | AI 피팅 요청      |
| `GET`  | `/fitting-sessions/{id}`        | AI 피팅 결과 조회   |




## 설계 포인트



### AI 실패가 서비스 실패가 되지 않도록

동선 추천은 **인터페이스 + 두 구현체** 구조입니다.

```
RouteRecommender
├── RuleBasedRouteRecommender   규칙 기반. 항상 성공
└── LlmRouteRecommender         @Primary. 실패 시 규칙 기반으로 위임
```

층 선정은 규칙이 담당하고 LLM은 **추천 사유 문장만** 생성합니다.
LLM이 존재하지 않는 층 코드를 반환해 FK 위반이 나는 상황을 원천 차단하기 위해서입니다.

API 키 미설정 · 타임아웃 · JSON 파싱 실패 · 검증 탈락 —
어떤 경우에도 폴백 문구로 대체되며 **발급 자체는 정상 완료**됩니다.

### 크레딧을 한 곳에서만 다루도록

크레딧을 변경하는 지점은 가입(적립) · 스캔(적립) · 피팅(차감·환급) 세 곳입니다.
각자 구현하면 원장과 잔액이 어긋나므로 `CreditService`로 통합했습니다.

```java
creditService.earn(userId, CreditPolicy.SCAN_AMOUNT, CreditReason.SCAN,
                   CreditRefType.VISIT_LOG, visitLogId, "MCM HAUS 방문");
```

원장 기록과 잔액 갱신이 하나의 트랜잭션으로 묶이며,
`refType + refId` 조합으로 중복 차감을 방지합니다.

### 콘텐츠를 코드가 아닌 데이터로

전시 층 구성, 설문 문항, 브랜드 스토리는 모두 DB에 있습니다.
실제로 기획이 여러 차례 바뀌었지만 (5개 층 → 4개 층, 3문항 → 6문항)
대부분 시드 데이터 교체만으로 대응할 수 있었습니다.

## 로컬 실행



### 요구사항

- JDK 21
- Docker · Docker Compose



### 실행

```bash
# 1. 저장소 클론
git clone https://github.com/Hsu-Likelion-14th-Hackathon/BE.git
cd BE

# 2. 환경 변수 설정
cp .env.example .env
# .env 파일을 열어 각 값을 채워주세요

# 3. 인프라 기동 (PostgreSQL · Redis)
docker compose up -d

# 4. 애플리케이션 실행
./gradlew bootRun
```



### 확인


|            | URL                                                                                        |
| ---------- | ------------------------------------------------------------------------------------------ |
| Swagger UI | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| API Docs   | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)                     |




## 배포

`main` 브랜치 푸시 시 자동 배포됩니다.

```
push main
   │
   ├─ Gradle bootJar 빌드 (테스트 제외)
   ├─ Docker 이미지 빌드 (linux/amd64)
   ├─ GHCR 푸시
   ├─ Azure VM SSH 접속 → 컨테이너 교체
   └─ Discord 알림
```

`dev` 브랜치로의 PR은 CI에서 PostgreSQL · Redis 서비스 컨테이너를 띄워
빌드와 테스트를 검증한 뒤에만 머지할 수 있습니다.

## 팀


| Product Manager | Designer  | FrontEnd | FrontEnd | BackEnd                  | BackEnd          |
| --------------- | --------- | -------- | -------- | ------------------------ | ---------------- |
| **임연주**         | **최소영**   | **김헌영**  | **김성빈**  | **박세웅**                  | **신채희**          |
| 기획 · 리서치 발표 피칭  | UX/UI 디자인 | 프론트엔드 총괄 | 프론트엔드    | 보딩패스 · AI 동선 도슨트 · Azure | 인증 · 상품 AI 가상 피팅 |


**1976년 뮌헨에서 태어난 이름 하나가, 2026년 다시 같은 질문을 던집니다.**

*우리는 어디로, 왜 떠나는가.*