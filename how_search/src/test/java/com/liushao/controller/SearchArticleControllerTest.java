package com.liushao.controller;

import com.liushao.pojo.Article;
import com.liushao.service.SearchArticleService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SearchArticleControllerTest {
    @Test
    void createAndUpdateBindFeignJsonBodiesInsteadOfSavingEmptyDocuments() throws Exception {
        SearchArticleService service = mock(SearchArticleService.class);
        SearchArticleController controller = new SearchArticleController();
        ReflectionTestUtils.setField(controller, "searchArticleService", service);
        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        String json = "{\"id\":\"article-1\",\"title\":\"中文标题\",\"content\":\"正文🙂\"}";
        mvc.perform(post("/search/article").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
        mvc.perform(put("/search/article/update").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk());
        ArgumentCaptor<Article> created = ArgumentCaptor.forClass(Article.class);
        ArgumentCaptor<Article> updated = ArgumentCaptor.forClass(Article.class);
        verify(service).add(created.capture());
        verify(service).update(updated.capture());
        for (Article article : new Article[]{created.getValue(), updated.getValue()}) {
            assertEquals("article-1", article.getId());
            assertEquals("中文标题", article.getTitle());
            assertEquals("正文🙂", article.getContent());
        }
    }
}
