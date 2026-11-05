package com.liushao.storage;

import com.liushao.dao.CommentDao;
import com.liushao.pojo.Comment;
import com.liushao.service.ArticleService;
import com.liushao.service.CommentService;
import com.liushao.util.IdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentVisibilityTest {
    @Test void publicReadsExcludePendingCommentsAndRequireAPublishedArticle() {
        var articles = mock(ArticleService.class); var dao = mock(CommentDao.class); var service = new CommentService();
        ReflectionTestUtils.setField(service, "articleService", articles); ReflectionTestUtils.setField(service, "commentDao", dao);
        var approved = new Comment(); approved.setState("1");
        when(dao.findByArticleidAndStateOrderByPublishdateAsc("article", "1")).thenReturn(List.of(approved));
        assertEquals(List.of(approved), service.findByArticleid("article"));
        when(articles.findPublicById("hidden")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        assertThrows(ResponseStatusException.class, () -> service.findByArticleid("hidden"));
        verify(dao, never()).findByArticleidAndStateOrderByPublishdateAsc("hidden", "1");
    }
    @Test void submittedCommentsCannotForgeApprovalOrAuditTime() {
        var service = new CommentService(); var dao = mock(CommentDao.class);
        ReflectionTestUtils.setField(service, "articleService", mock(ArticleService.class));
        ReflectionTestUtils.setField(service, "commentDao", dao); ReflectionTestUtils.setField(service, "idWorker", new IdWorker(1, 1));
        var input = new Comment(); input.setArticleid("article"); input.setContent("content"); input.setState("1"); input.setChecktime(new Date());
        service.add(input); assertEquals("0", input.getState()); assertNull(input.getChecktime());
        input.setContent(" "); assertThrows(IllegalArgumentException.class, () -> service.add(input));
    }
}
