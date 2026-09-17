# Lunch Pick

오늘의 기분, 음식 장르, 예산, 맵기와 동행 여부로 점심 메뉴를 추천하는 풀스택 앱입니다.

## 구성

- `frontend`: React + Vite, Vercel 배포
- `backend`: Java 21 + Spring Boot 3.5 + Spring Data JPA, Render 배포
- `PostgreSQL`: 메뉴와 추천 이력 저장
- `RecommendationProvider`: 향후 Gemini 추천 구현을 꽂을 수 있는 전략 인터페이스

## 배포 주소

- 프론트엔드: <https://lunch-project.vercel.app>
- 백엔드 API: <https://lunch-project-api.onrender.com>
- 운영 DB: Neon PostgreSQL 17 (Singapore)

## 로컬 실행

`docker compose up -d postgres`로 DB를 실행한 뒤 `backend`에서 `mvn spring-boot:run`, `frontend`에서 `npm install && npm run dev`를 실행합니다.

## Gemini 연동 지점

`RecommendationProvider`를 구현하는 `GeminiRecommendationProvider`를 추가하고 `app.recommendation.provider=gemini` 조건을 연결하면 됩니다. API 키는 코드에 넣지 않고 Render의 `GEMINI_API_KEY` secret 환경변수에 설정합니다. 현재는 `RulesRecommendationProvider`가 동작합니다.

## 지도 연동 지점

`RestaurantSearchProvider`가 카카오 Local/Map API 또는 네이버 지도/검색 API 어댑터의 계약입니다. 현재 UI에는 준비 상태만 노출하며, 실제 키를 발급받은 뒤 `KAKAO_REST_API_KEY` 또는 `NAVER_MAP_CLIENT_ID`, `NAVER_MAP_CLIENT_SECRET`을 Render secret으로 설정하도록 구성했습니다. 위치 권한은 브라우저에서 사용자가 명시적으로 허용한 경우에만 요청해야 합니다.

## 배포 시 참고

- Render Blueprint는 루트의 `render.yaml`을 사용합니다. DB 접속값은 secret 환경변수로 입력합니다.
- 프론트 환경변수 `VITE_API_BASE_URL`에는 실제 Render 서비스 URL을 입력합니다.
- 운영 DB는 기존 Render 프로젝트와 데이터를 섞지 않도록 별도의 Neon PostgreSQL 프로젝트로 분리했습니다.
