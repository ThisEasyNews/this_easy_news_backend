package com.thiseasynews.server.controller;

import com.thiseasynews.server.dto.response.NewsResponse;
import com.thiseasynews.server.global.common.PageResponse;
import com.thiseasynews.server.global.exception.BusinessException;
import com.thiseasynews.server.global.exception.ErrorCode;
import com.thiseasynews.server.service.NewsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @Test
    @DisplayName("GET /api/news/{id} - 기사 상세 반환 200")
    void getArticle_200() throws Exception {
        NewsResponse response = NewsResponse.builder()
                .id(1L)
                .originalTitle("AI 기사 제목")
                .url("https://example.com/1")
                .content("기사 본문")
                .mediaName("조선일보")
                .publishedAt(LocalDateTime.now())
                .build();

        given(newsService.getArticle(1L)).willReturn(response);

        mockMvc.perform(get("/api/news/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.originalTitle").value("AI 기사 제목"))
                .andExpect(jsonPath("$.data.content").value("기사 본문"));
    }

    @Test
    @DisplayName("GET /api/news/{id} - 없는 기사 404")
    void getArticle_404() throws Exception {
        given(newsService.getArticle(anyLong()))
                .willThrow(new BusinessException(ErrorCode.ARTICLE_NOT_FOUND));

        mockMvc.perform(get("/api/news/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("A001"));
    }

    @Test
    @DisplayName("GET /api/news/media/{mediaId} - 언론사별 기사 목록 200")
    void getArticlesByMedia_200() throws Exception {
        List<NewsResponse> items = List.of(
                NewsResponse.builder().id(1L).originalTitle("기사1").mediaName("조선일보").build(),
                NewsResponse.builder().id(2L).originalTitle("기사2").mediaName("조선일보").build()
        );
        PageResponse<NewsResponse> page = PageResponse.of(
                new PageImpl<>(items));

        given(newsService.getArticlesByMedia("MED_CHOSUN", 0, 20)).willReturn(page);

        mockMvc.perform(get("/api/news/media/MED_CHOSUN")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/news/category/{categoryId} - 카테고리별 기사 목록 200")
    void getArticlesByCategory_200() throws Exception {
        PageResponse<NewsResponse> page = PageResponse.of(new PageImpl<>(
                List.of(NewsResponse.builder().id(1L).originalTitle("경제 기사").build())
        ));
        given(newsService.getArticlesByCategory("CAT_ECONOMY", 0, 20)).willReturn(page);

        mockMvc.perform(get("/api/news/category/CAT_ECONOMY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @DisplayName("GET /api/news/search - 복합 검색 200")
    void searchArticles_200() throws Exception {
        PageResponse<NewsResponse> page = PageResponse.of(new PageImpl<>(List.of()));
        given(newsService.searchArticles(any())).willReturn(page);

        mockMvc.perform(get("/api/news/search")
                        .param("mediaId",    "MED_CHOSUN")
                        .param("categoryId", "CAT_ECONOMY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
