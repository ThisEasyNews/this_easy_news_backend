package com.thiseasynews.server.service;

import com.thiseasynews.server.dto.response.NewsResponse;
import com.thiseasynews.server.entity.Article;
import com.thiseasynews.server.entity.CommonDetail;
import com.thiseasynews.server.global.common.PageResponse;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @InjectMocks
    private NewsService newsService;

    @Mock
    private ArticleRepository articleRepository;

    @Test
    @DisplayName("기사 단건 상세 조회 - 본문 포함 반환")
    void getArticle_success() {
        Article article = makeArticle(1L, "AI 기사 제목", "MED_CHOSUN", "조선일보");
        given(articleRepository.findPublishedById(1L)).willReturn(Optional.of(article));

        NewsResponse result = newsService.getArticle(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("AI 기사 제목");
        assertThat(result.getMediaName()).isEqualTo("조선일보");
        assertThat(result.getContent()).isEqualTo("기사 본문");
    }

    @Test
    @DisplayName("존재하지 않는 기사 조회 시 ARTICLE_NOT_FOUND 예외")
    void getArticle_notFound() {
        given(articleRepository.findPublishedById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.getArticle(999L))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.ARTICLE_NOT_FOUND));
    }

    @Test
    @DisplayName("언론사별 기사 목록 Specification 조회 - 페이징 반환")
    void getArticlesByMedia_success() {
        List<Article> articles = List.of(
                makeArticle(1L, "기사1", "MED_CHOSUN", "조선일보"),
                makeArticle(2L, "기사2", "MED_CHOSUN", "조선일보")
        );
        given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(articles));

        PageResponse<NewsResponse> result = newsService.getArticlesByMedia("MED_CHOSUN", 0, 20);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.isFirst()).isTrue();
    }

    @Test
    @DisplayName("카테고리별 기사 목록 Specification 조회")
    void getArticlesByCategory_success() {
        given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(makeArticle(1L, "경제 기사", "MED_MK", "매일경제"))));

        PageResponse<NewsResponse> result = newsService.getArticlesByCategory("CAT_ECONOMY", 0, 20);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("키워드별 기사 목록 Specification 조회")
    void getArticlesByKeyword_success() {
        given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(makeArticle(1L, "AI 기사", "MED_CHOSUN", "조선일보"))));

        PageResponse<NewsResponse> result = newsService.getArticlesByKeyword(1, 0, 20);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("페이지 크기는 최대 20으로 제한된다")
    void fetchPage_sizeIsCapped() {
        given(articleRepository.findAll(any(Specification.class), any(Pageable.class)))
            .willReturn(Page.empty());
        newsService.getArticlesByMedia("MED_CHOSUN", 0, 100);

        then(articleRepository).should().findAll(
            any(Specification.class),
            ArgumentMatchers.<Pageable>argThat(p -> p != null && p.getPageSize() == 20)
        );
    }


    // ── 헬퍼 ──────────────────────────────────────────
    private Article makeArticle(Long id, String title, String mediaId, String mediaName) {
        CommonDetail media = new CommonDetail();
        ReflectionTestUtils.setField(media, "id",         mediaId);
        ReflectionTestUtils.setField(media, "name",       mediaName);
        ReflectionTestUtils.setField(media, "statusCode", "PUBLISHED");

        Article article = new Article();
        ReflectionTestUtils.setField(article, "id",             id);
        ReflectionTestUtils.setField(article, "originalTitle",  title);
        ReflectionTestUtils.setField(article, "url",            "https://example.com/" + id);
        ReflectionTestUtils.setField(article, "crawlerContent", "기사 본문");
        ReflectionTestUtils.setField(article, "publishedAt",    LocalDateTime.now());
        ReflectionTestUtils.setField(article, "media",          media);
        ReflectionTestUtils.setField(article, "category",       null);
        ReflectionTestUtils.setField(article, "summary",        null);
        ReflectionTestUtils.setField(article, "statusCode",     "PUBLISHED");
        return article;
    }
}
