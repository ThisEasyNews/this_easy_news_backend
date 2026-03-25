# This Easy News — Backend

> 바쁜 직장인을 위한 3분 핵심 뉴스 요약 서비스

---

## 📁 패키지 구조

```
src/main/java/com/thiseasynews/
├── ThisEasyNewsApplication.java
└── server/
    ├── controller/
    │   ├── NewsController          # 기사 조회, 언론사/카테고리별 필터링
    │   ├── SummaryController       # 뉴스 요약 및 3분 브리핑 조회
    │   ├── KeywordController       # 핫 키워드 Top10 및 키워드 통계 조회
    │   ├── CommonCodeController    # 언론사·카테고리 목록 등 코드 조회
    │   └── BatchLogController      # 배치 실행 이력 (Admin)
    │
    ├── service/
    │   ├── NewsService             # Article 관련 로직
    │   ├── SummaryService          # NewsSummary(General/Briefing) 가공 로직
    │   ├── KeywordService          # Keyword 및 KeywordLog(통계) 처리 로직
    │   ├── CommonCodeService       # 공통 코드(Group/Detail) 조회 로직
    │   └── BatchLogService         # 배치 실행 결과 조회/기록 로직
    │
    ├── repository/
    │   ├── ArticleRepository       # + JpaSpecificationExecutor
    │   ├── ArticleSpecification    # Specification 조건 팩토리 모음
    │   ├── NewsSummaryRepository
    │   ├── NewsKeywordRepository
    │   ├── KeywordLogRepository
    │   ├── CommonGroupRepository
    │   ├── CommonDetailRepository
    │   └── BatchLogRepository
    │
    ├── entity/                     # 8개 엔티티 (BaseTimeEntity 상속)
    │
    ├── dto/
    │   ├── request/ArticleSearchRequest
    │   └── response/               # NewsResponse · BriefingResponse · KeywordResponse · CodeResponse · BatchLogResponse
    │
    └── global/
        ├── common/                 # BaseTimeEntity · ApiResponse · PageResponse
        ├── config/                 # JpaConfig · RedisConfig · SwaggerConfig
        └── exception/              # ErrorCode · BusinessException · GlobalExceptionHandler
```

---

## 🗺️ API 엔드포인트

| 화면 | Method | Endpoint | 캐시 |
|------|--------|----------|------|
| 기사 상세 | GET | `/api/news/{id}` | - |
| 언론사별 기사 | GET | `/api/news/media/{mediaId}` | - |
| 카테고리별 기사 | GET | `/api/news/category/{categoryId}` | - |
| 복합 검색 | GET | `/api/news/search?mediaId=&categoryId=&keywordId=` | - |
| 오늘 브리핑 목록 | GET | `/api/summaries/briefings/today` | Redis 30분 |
| 날짜별 브리핑 목록 | GET | `/api/summaries/briefings/date/{date}` | - |
| 브리핑 상세 | GET | `/api/summaries/briefings/{id}` | Redis 30분 |
| 핫 키워드 | GET | `/api/keywords/hot` | Redis 10분 |
| 날짜별 키워드 | GET | `/api/keywords/hot/date/{date}` | - |
| 키워드 기사 | GET | `/api/keywords/{keywordId}/articles` | - |
| 언론사 목록 | GET | `/api/codes/media` | Redis 1시간 |
| 카테고리 목록 | GET | `/api/codes/categories` | Redis 1시간 |
| 그룹별 코드 | GET | `/api/codes/groups/{groupId}` | - |
| 배치 이력 | GET | `/api/admin/batch-logs` | - |
| Job 이력 | GET | `/api/admin/batch-logs/jobs/{jobName}` | - |

---

## ⚙️ JPA Specification 사용 방식

```java
// 단일 조건
Specification<Article> spec = ArticleSpecification.published()
        .and(ArticleSpecification.byMedia("MED_CHOSUN"));

// 동적 복합 조건 (searchArticles)
Specification<Article> spec = ArticleSpecification.published();
if (req.getMediaId()    != null) spec = spec.and(ArticleSpecification.byMedia(req.getMediaId()));
if (req.getCategoryId() != null) spec = spec.and(ArticleSpecification.byCategory(req.getCategoryId()));
if (req.getKeywordId()  != null) spec = spec.and(ArticleSpecification.byKeyword(req.getKeywordId()));

Page<Article> result = articleRepository.findAll(spec, pageable);
```

---

## 💾 응답 포맷

```json
// 성공 (단일)
{ "success": true, "message": "success", "data": { ... } }

// 성공 (리스트 - briefings/today, briefings/date/{date}, keywords/hot 등)
{ "success": true, "message": "success", "data": [ { ... }, { ... } ] }

// 에러
{ "success": false, "error": { "code": "A001", "message": "기사를 찾을 수 없습니다." } }
```

---

## 🚀 빠른 시작

```bash
psql -U postgres -c "CREATE DATABASE this_easy_news;"
psql -U postgres -d this_easy_news -f src/main/resources/init.sql
export DB_USERNAME=postgres && export DB_PASSWORD=yourpassword
./gradlew bootRun
# Swagger: http://localhost:8080/swagger-ui.html
```

---

## 🔧 배치 연동

```java
batchLogService.run("RSS_CRAWLING", () -> rssCrawlService.crawl());
summaryService.evictTodayBriefing(); // 브리핑 배치 완료 후 캐시 무효화
```
