package com.liushao.storage;

import com.liushao.client.SearchClient;
import com.liushao.dao.ArticleDao;
import com.liushao.entity.Result;
import com.liushao.pojo.Article;
import com.liushao.service.ArticleService;
import com.liushao.util.IdWorker;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.*;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(ArticleLifecycleTest.Config.class)
class ArticleLifecycleTest {
    @Autowired ArticleService articles;
    @Autowired ArticleDao dao;
    @Autowired SearchClient search;
    @Autowired RedisTemplate<String, Object> redis;

    @BeforeEach void resetState() {
        dao.deleteAll();
        reset(search, redis);
        when(redis.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(search.saveArticle(any())).thenReturn(new Result(true, 20000, "ok"));
        when(search.deleteArticle(anyString())).thenReturn(new Result(true, 20000, "ok"));
    }

    @Test void newArticleHasServerDefaultsAndIsNotPublicEvenIfClientRequestsApproval() {
        Article input = input("  中文标题🙂  "); input.setState("1"); input.setThumbup(999);
        Article saved = articles.add(input);
        assertEquals("中文标题🙂", saved.getTitle());
        assertEquals("0", saved.getState()); assertEquals("1", saved.getIspublic());
        assertEquals(0, saved.getThumbup()); assertEquals(0, saved.getVisits());
        assertNotNull(saved.getCreatetime()); assertNotNull(saved.getUpdatetime());
        assertEquals(0, articles.findPublic(1, 10, null, null).getTotalElements());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> articles.findPublicById(saved.getId())).getStatusCode().value());
    }

    @Test void reviewIndexesFreshStateAndInvalidatesPreviouslyReadCache() {
        Article saved = articles.add(input("review"));
        articles.findById(saved.getId());
        articles.examine(saved.getId());
        verify(search).saveArticle(argThat(a -> "1".equals(a.getState()) && a.getId().equals(saved.getId())));
        verify(redis).delete("article_" + saved.getId());
        assertEquals("1", articles.findPublicById(saved.getId()).getState());
    }

    @Test void draftsCanBeEditedAndDeletedWithoutSearchService() {
        Article draft = articles.add(input("draft"));
        Article update = input("updated draft"); update.setId(draft.getId());
        articles.update(update);
        assertEquals("updated draft", dao.findById(draft.getId()).orElseThrow().getTitle());
        articles.deleteById(draft.getId());
        verifyNoInteractions(search);
    }

    @Test void failedIndexSyncRollsBackApprovalAndDoesNotEvictCache() {
        Article saved = articles.add(input("failure"));
        when(search.saveArticle(any())).thenReturn(new Result(false, 20001, "offline"));
        assertThrows(IllegalStateException.class, () -> articles.examine(saved.getId()));
        assertEquals("0", dao.findById(saved.getId()).orElseThrow().getState());
        verify(redis, never()).delete(anyString());
    }

    @Test void editingKeepsCountersAndCreationTimeAndCanHideOrRepublish() {
        Article saved = articles.add(input("edit"));
        articles.examine(saved.getId()); articles.updateThumbup(saved.getId());
        Article update = input("edited"); update.setId(saved.getId()); update.setIspublic("0"); update.setState("0"); update.setThumbup(100);
        articles.update(update);
        Article stored = dao.findById(saved.getId()).orElseThrow();
        assertEquals(1, stored.getThumbup()); assertEquals(saved.getCreatetime().getTime(), stored.getCreatetime().getTime());
        assertEquals("1", stored.getState());
        verify(search).deleteArticle(saved.getId());
        assertThrows(ResponseStatusException.class, () -> articles.findPublicById(saved.getId()));
        update.setIspublic("1"); articles.update(update);
        assertEquals("edited", articles.findPublicById(saved.getId()).getTitle());
    }

    @Test void publicQueriesUseExactChannelAndLiteralKeywordsAndExcludePrivateArticles() {
        Article first = input("literal_100%"); first.setChannelid("1"); articles.add(first); articles.examine(first.getId());
        Article second = input("literalX100z"); second.setChannelid("11"); articles.add(second); articles.examine(second.getId());
        Article hidden = input("literal_100% hidden"); hidden.setIspublic("0"); articles.add(hidden); articles.examine(hidden.getId());
        assertEquals(1, articles.findPublic(1, 10, "1", null).getTotalElements());
        assertEquals(1, articles.findPublic(1, 10, null, "_100%").getTotalElements());
        assertEquals(2, articles.findPublished().size());
        assertEquals(1, articles.findSearch(Map.of("channelid", "1"), 1, 10).getTotalElements());
    }

    @Test void invalidContentPagingAndMissingIdsDoNotSucceed() {
        Article invalid = input(" "); assertThrows(IllegalArgumentException.class, () -> articles.add(invalid));
        assertThrows(IllegalArgumentException.class, () -> articles.findPublic(0, 10, null, null));
        assertThrows(IllegalArgumentException.class, () -> articles.findPublic(1, 101, null, null));
        assertThrows(ResponseStatusException.class, () -> articles.examine("missing"));
        assertThrows(ResponseStatusException.class, () -> articles.updateThumbup("missing"));
        assertEquals(0, dao.count());
    }

    @Test void deletingPublishedArticleRemovesItFromBothStoresAndCache() {
        Article saved = articles.add(input("delete")); articles.examine(saved.getId());
        articles.deleteById(saved.getId());
        assertFalse(dao.existsById(saved.getId())); verify(search).deleteArticle(saved.getId());
        assertThrows(ResponseStatusException.class, () -> articles.findPublicById(saved.getId()));
    }

    private Article input(String title) {
        Article article = new Article(); article.setTitle(title); article.setContent("正文🙂\n".repeat(100)); return article;
    }

    @Configuration @EnableTransactionManagement
    @EnableJpaRepositories(basePackageClasses = ArticleDao.class,
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ArticleDao.class))
    static class Config {
        @Bean DataSource dataSource() { return new DriverManagerDataSource("jdbc:h2:mem:article-lifecycle;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", ""); }
        @Bean LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource source) {
            var factory = new LocalContainerEntityManagerFactoryBean(); factory.setDataSource(source);
            factory.setPackagesToScan("com.liushao.pojo"); factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaPropertyMap(Map.of("hibernate.hbm2ddl.auto", "create-drop")); return factory;
        }
        @Bean PlatformTransactionManager transactionManager(EntityManagerFactory factory) { return new JpaTransactionManager(factory); }
        @Bean ArticleService articleService() { return new ArticleService(); }
        @Bean IdWorker idWorker() { return new IdWorker(1, 1); }
        @Bean SearchClient searchClient() { return mock(SearchClient.class); }
        @Bean RedisTemplate<String, Object> redisTemplate() { return mock(RedisTemplate.class); }
    }
}
