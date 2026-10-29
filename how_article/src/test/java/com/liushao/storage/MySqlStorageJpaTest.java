package com.liushao.storage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushao.dao.CommentDao;
import com.liushao.dao.SpitDao;
import com.liushao.pojo.Comment;
import com.liushao.pojo.Spit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Uses a private in-memory database and only the migrated entities/repositories.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = MySqlStorageJpaTest.JpaTestConfiguration.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:comment-spit-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.jpa.show-sql=false"
})
class MySqlStorageJpaTest {

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private SpitDao spitDao;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void storesSqlIdsAndLongTextWhileKeepingLegacyJsonIds() throws Exception {
        String content = String.join("", Collections.nCopies(1024, "long-text-\u8bc4\u8bba-\uD83D\uDE42"));
        Comment comment = comment("comment-1", "article-1", content);
        Spit spit = spit("spit-1", null);
        spit.setContent(content);
        commentDao.saveAndFlush(comment);
        spitDao.saveAndFlush(spit);
        entityManager.clear();

        Comment storedComment = commentDao.findById("comment-1").get();
        Spit storedSpit = spitDao.findById("spit-1").get();
        assertEquals(content, storedComment.getContent());
        assertEquals(content, storedSpit.getContent());
        assertEquals("comment-1", jdbcTemplate.queryForObject(
                "select id from tb_comment where id = ?", String.class, "comment-1"));
        assertEquals("spit-1", jdbcTemplate.queryForObject(
                "select id from tb_spit where id = ?", String.class, "spit-1"));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode commentJson = mapper.readTree(mapper.writeValueAsString(storedComment));
        JsonNode spitJson = mapper.readTree(mapper.writeValueAsString(storedSpit));
        assertEquals("comment-1", commentJson.path("_id").asText());
        assertEquals("spit-1", spitJson.path("_id").asText());
        assertFalse(commentJson.has("id"));
        assertFalse(spitJson.has("id"));
    }

    @Test
    void incrementsPersistedCountersAndDoesNotUpdateOtherRows() {
        spitDao.saveAndFlush(spit("parent", null));
        spitDao.saveAndFlush(spit("other", null));

        for (int i = 0; i < 3; i++) {
            assertEquals(1, spitDao.incrementComment("parent"));
        }
        for (int i = 0; i < 4; i++) {
            assertEquals(1, spitDao.incrementThumbup("parent"));
        }
        assertEquals(0, spitDao.incrementComment("missing"));
        assertEquals(0, spitDao.incrementThumbup("missing"));
        entityManager.clear();

        Spit parent = spitDao.findById("parent").get();
        Spit other = spitDao.findById("other").get();
        assertEquals(Integer.valueOf(3), parent.getComment());
        assertEquals(Integer.valueOf(4), parent.getThumbup());
        assertEquals(Integer.valueOf(0), other.getComment());
        assertEquals(Integer.valueOf(0), other.getThumbup());
    }

    @Test
    void persistsExplicitNullCountersAsZeroOnInsertAndUpdate() {
        Spit spit = spit("null-counters", null);
        clearCounters(spit);
        spitDao.saveAndFlush(spit);
        entityManager.clear();

        Spit inserted = spitDao.findById("null-counters").get();
        assertZeroCounters(inserted);
        clearCounters(inserted);
        spitDao.saveAndFlush(inserted);
        entityManager.clear();
        assertZeroCounters(spitDao.findById("null-counters").get());
    }

    @Test
    void reviewPersistsStateAndCheckTimeForOnlyTheRequestedComment() {
        commentDao.saveAndFlush(comment("review", "article-1", "needs review"));
        commentDao.saveAndFlush(comment("untouched", "article-1", "still pending"));
        Date checktime = Date.from(Instant.parse("2026-10-29T04:05:06Z"));

        assertEquals(1, commentDao.examine("review", checktime));
        assertEquals(0, commentDao.examine("missing", checktime));
        entityManager.clear();

        Comment reviewed = commentDao.findById("review").get();
        assertEquals("1", reviewed.getState());
        assertEquals(checktime, reviewed.getChecktime());
        assertEquals("0", commentDao.findById("untouched").get().getState());
    }

    @Test
    void filtersCommentsByArticleAndPagesTheSameQueryByExample() {
        commentDao.saveAndFlush(comment("comment-1", "article-1", "matching text one"));
        commentDao.saveAndFlush(comment("comment-2", "article-1", "matching text two"));
        commentDao.saveAndFlush(comment("comment-3", "article-2", "matching text other"));

        assertEquals(2, commentDao.findByArticleid("article-1").size());
        assertTrue(commentDao.findByArticleid("missing").isEmpty());
        Comment probe = new Comment();
        probe.setArticleid("article-1");
        probe.setState("0");
        probe.setContent("matching");
        Example<Comment> example = Example.of(probe, ExampleMatcher.matching()
                .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains()));
        Page<Comment> page = commentDao.findAll(example,
                PageRequest.of(1, 1, Sort.by("content")));

        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals("comment-2", page.getContent().get(0).get_id());
    }

    @Test
    void pagesRepliesByTheirParentWithoutReturningOtherThreads() {
        spitDao.saveAndFlush(spit("reply-1", "parent"));
        spitDao.saveAndFlush(spit("reply-2", "parent"));
        spitDao.saveAndFlush(spit("reply-3", "other-parent"));

        Page<Spit> page = spitDao.findByParentid("parent",
                PageRequest.of(1, 1, Sort.by("content")));

        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals("reply-2", page.getContent().get(0).get_id());
        assertTrue(spitDao.findByParentid("missing", PageRequest.of(0, 5)).isEmpty());
    }

    @Test
    void deletingMissingCommentsAndSpitsRemainsAnIdempotentNoOp() {
        commentDao.saveAndFlush(comment("delete-comment", "article-1", "delete me"));
        commentDao.saveAndFlush(comment("keep-comment", "article-1", "keep me"));
        spitDao.saveAndFlush(spit("delete-spit", null));
        spitDao.saveAndFlush(spit("keep-spit", null));

        assertEquals(1, commentDao.deleteExistingById("delete-comment"));
        assertEquals(0, commentDao.deleteExistingById("delete-comment"));
        assertEquals(0, commentDao.deleteExistingById("missing"));
        assertEquals(1, spitDao.deleteExistingById("delete-spit"));
        assertEquals(0, spitDao.deleteExistingById("delete-spit"));
        assertEquals(0, spitDao.deleteExistingById("missing"));
        entityManager.clear();

        assertFalse(commentDao.findById("delete-comment").isPresent());
        assertFalse(spitDao.findById("delete-spit").isPresent());
        assertTrue(commentDao.findById("keep-comment").isPresent());
        assertTrue(spitDao.findById("keep-spit").isPresent());
    }

    private static Comment comment(String id, String articleid, String content) {
        Comment comment = new Comment();
        comment.set_id(id);
        comment.setArticleid(articleid);
        comment.setUserid("user-1");
        comment.setContent(content);
        comment.setPublishdate(new Date());
        comment.setState("0");
        return comment;
    }

    private static Spit spit(String id, String parentid) {
        Spit spit = new Spit();
        spit.set_id(id);
        spit.setParentid(parentid);
        spit.setUserid("user-1");
        spit.setContent(id + " reply text");
        spit.setPublishtime(new Date());
        spit.setState("1");
        return spit;
    }

    private static void clearCounters(Spit spit) {
        spit.setVisits(null);
        spit.setThumbup(null);
        spit.setShare(null);
        spit.setComment(null);
    }

    private static void assertZeroCounters(Spit spit) {
        assertEquals(Integer.valueOf(0), spit.getVisits());
        assertEquals(Integer.valueOf(0), spit.getThumbup());
        assertEquals(Integer.valueOf(0), spit.getShare());
        assertEquals(Integer.valueOf(0), spit.getComment());
    }

    @SpringBootConfiguration
    @EnableJpaRepositories(basePackageClasses = {CommentDao.class, SpitDao.class},
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                    classes = {CommentDao.class, SpitDao.class}))
    static class JpaTestConfiguration {

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource);
            factory.setPersistenceXmlLocation("classpath:META-INF/comment-spit-persistence.xml");
            factory.setPersistenceUnitName("comment-spit-test");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            Map<String, Object> properties = new HashMap<>();
            properties.put("hibernate.hbm2ddl.auto", "create-drop");
            properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            factory.setJpaPropertyMap(properties);
            return factory;
        }
    }
}
