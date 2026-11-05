package com.liushao.service;

import com.liushao.client.SearchClient;
import com.liushao.dao.ArticleDao;
import com.liushao.dao.CommentDao;
import com.liushao.dao.ArticleCardProjection;
import com.liushao.content.ArticleContent;
import com.liushao.vo.PublicArticleSummary;
import com.liushao.vo.PublicArticleDetail;
import com.liushao.entity.Result;
import com.liushao.pojo.Article;
import com.liushao.util.IdWorker;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ArticleService {
    @Autowired private ArticleDao articleDao;
    @Autowired private CommentDao commentDao;
    @Autowired private SearchClient searchClient;
    @Autowired private IdWorker idWorker;
    @Autowired private ArticleContent content;
    @Resource private RedisTemplate<String, Object> redisTemplate;

    public List<Article> findAll() { return articleDao.findAll(); }

    public Page<Article> findSearch(Map<String, Object> filters, int page, int size) {
        validatePage(page, size);
        return articleDao.findAll(createSpecification(filters), PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "updatetime", "id")));
    }

    public List<Article> findSearch(Map<String, Object> filters) {
        return articleDao.findAll(createSpecification(filters));
    }

    public List<Article> findPublished() { return articleDao.findAll(published()); }

    public Page<PublicArticleSummary> findPublic(int page, int size, String channelid, String keyword) {
        validatePage(page, size);
        Specification<Article> spec = published();
        if (channelid != null && !channelid.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("channelid"), channelid));
        }
        if (keyword != null && !keyword.isBlank()) {
            String pattern = "%" + escapeLike(keyword.trim()) + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(root.get("title"), pattern, '\\'), cb.like(root.get("content"), pattern, '\\')));
        }
        var pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "istop", "updatetime", "id"));
        return articleDao.findBy(spec, query -> query.as(ArticleCardProjection.class).page(pageable))
                .map(row -> new PublicArticleSummary(row.getId(), row.getTitle(), content.summary(row.getSummary(), row.getExcerpt()),
                        row.getImage(), row.getChannelid(), row.getColumnid(), row.getIstop(), row.getCreatetime(), row.getUpdatetime()));
    }

    public PublicArticleDetail publicDetail(String id) {
        Article article = findPublicById(id);
        String format = content.format(article.getContentformat());
        return new PublicArticleDetail(article.getId(), article.getTitle(), article.getContent(), format,
                content.render(article.getContent(), format), content.summary(article.getSummary(), article.getExcerpt()),
                article.getImage(), article.getChannelid(), article.getColumnid(), article.getIstop(), article.getState(),
                article.getIspublic(), article.getCreatetime(), article.getUpdatetime());
    }

    public Article findPublicById(String id) {
        // Recheck publication in MySQL even after a previous read was cached.
        return articleDao.findOne(published().and((root, query, cb) -> cb.equal(root.get("id"), id)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在或尚未发布"));
    }

    public Article findById(String id) {
        Article cached = (Article) redisTemplate.opsForValue().get("article_" + id);
        if (cached != null) return cached;
        Article article = requireArticle(id);
        redisTemplate.opsForValue().set("article_" + id, article, 1, TimeUnit.DAYS);
        return article;
    }

    @Transactional
    public Article add(Article article) {
        validateContent(article);
        article.setContentformat(content.format(article.getContentformat()));
        article.setExcerpt(content.excerpt(article.getContent(), article.getContentformat()));
        article.setId(String.valueOf(idWorker.nextId()));
        article.setCreatetime(now());
        article.setUpdatetime(article.getCreatetime());
        article.setState("0");
        article.setIspublic(flag(article.getIspublic(), "1"));
        article.setIstop(flag(article.getIstop(), "0"));
        article.setVisits(0);
        article.setThumbup(0);
        article.setComment(0);
        return articleDao.save(article);
    }

    @Transactional
    public void update(Article input) {
        validateContent(input);
        Article article = requireArticle(input.getId());
        article.setContentformat(content.format(input.getContentformat() == null ? article.getContentformat() : input.getContentformat()));
        if (input.getSummary() != null) article.setSummary(input.getSummary());
        article.setExcerpt(content.excerpt(input.getContent(), article.getContentformat()));
        article.setTitle(input.getTitle());
        article.setContent(input.getContent());
        article.setImage(input.getImage());
        article.setChannelid(input.getChannelid());
        article.setColumnid(input.getColumnid());
        article.setUrl(input.getUrl());
        article.setType(input.getType());
        article.setIspublic(flag(input.getIspublic(), article.getIspublic()));
        article.setIstop(flag(input.getIstop(), article.getIstop()));
        article.setUpdatetime(now());
        articleDao.saveAndFlush(article);
        if ("1".equals(article.getState())) syncIndex(article);
        invalidateAfterCommit(article.getId());
    }

    @Transactional
    public void deleteById(String id) {
        Article article = requireArticle(id);
        if ("1".equals(article.getState())) requireSearchSuccess(searchClient.deleteArticle(id));
        commentDao.deleteByArticleid(id);
        articleDao.deleteById(id);
        invalidateAfterCommit(id);
    }

    @Transactional
    public void examine(String id) {
        Article article = requireArticle(id);
        article.setState("1");
        article.setUpdatetime(now());
        articleDao.saveAndFlush(article);
        syncIndex(article);
        invalidateAfterCommit(id);
    }

    @Transactional
    public int updateThumbup(String id) {
        requireArticle(id);
        int updated = articleDao.updateThumbup(id);
        invalidateAfterCommit(id);
        return updated;
    }

    private void syncIndex(Article article) {
        requireSearchSuccess("1".equals(article.getState()) && "1".equals(article.getIspublic())
                ? searchClient.saveArticle(article) : searchClient.deleteArticle(article.getId()));
    }

    private void requireSearchSuccess(Result result) {
        if (result == null || !result.isFlag())
            throw new IllegalStateException("搜索索引同步失败，请稍后重试");
    }

    private void invalidateAfterCommit(String id) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { redisTemplate.delete("article_" + id); }
        });
    }

    private Article requireArticle(String id) {
        return articleDao.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在"));
    }

    private Specification<Article> published() {
        return (root, query, cb) -> cb.and(cb.equal(root.get("state"), "1"), cb.equal(root.get("ispublic"), "1"));
    }

    private void validateContent(Article article) {
        if (article.getTitle() == null || article.getTitle().isBlank() || article.getTitle().trim().length() > 100)
            throw new IllegalArgumentException("标题不能为空且不能超过100字");
        if (article.getContent() == null || article.getContent().isBlank())
            throw new IllegalArgumentException("正文不能为空");
        if (article.getContent().length() > ArticleContent.MAX_CONTENT) throw new IllegalArgumentException("正文不能超过20万字符");
        if (article.getSummary() != null && article.getSummary().codePointCount(0, article.getSummary().length()) > 300)
            throw new IllegalArgumentException("摘要不能超过300字");
        if (article.getImage() != null && article.getImage().length() > 100) throw new IllegalArgumentException("封面链接不能超过100字符");
        if (article.getImage() != null && !article.getImage().isBlank()) {
            String image = article.getImage().trim();
            boolean local = image.matches("/api/article/media/[a-f0-9]{32}\\.(png|jpg)");
            try {
                var uri = java.net.URI.create(image);
                boolean external = ("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                        && uri.getHost() != null && uri.getUserInfo() == null;
                if (!local && !external) throw new IllegalArgumentException("封面需使用已上传图片或http/https链接");
            } catch (IllegalArgumentException e) { throw new IllegalArgumentException("封面需使用已上传图片或有效的http/https链接"); }
            article.setImage(image);
        }
        article.setTitle(article.getTitle().trim());
    }

    private String flag(String value, String fallback) {
        if (value == null) return fallback == null ? "0" : fallback;
        if (!"0".equals(value) && !"1".equals(value)) throw new IllegalArgumentException("状态只能是0或1");
        return value;
    }

    // The existing MySQL DATETIME columns have second precision. Avoid server-side
    // rounding changing the creation time between the create response and a later read.
    private Date now() { return new Date(System.currentTimeMillis() / 1000 * 1000); }

    private void validatePage(int page, int size) {
        if (page < 1 || size < 1 || size > 100) throw new IllegalArgumentException("页码至少为1，每页数量须在1到100之间");
    }

    private String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private Specification<Article> createSpecification(Map<String, Object> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String field : List.of("id", "columnid", "userid", "channelid", "ispublic", "istop", "state", "type")) {
                Object value = filters.get(field);
                if (value != null && !value.toString().isBlank()) predicates.add(cb.equal(root.get(field), value.toString()));
            }
            for (String field : List.of("title", "content", "image", "url")) {
                Object value = filters.get(field);
                if (value != null && !value.toString().isBlank())
                    predicates.add(cb.like(root.get(field), "%" + escapeLike(value.toString()) + "%", '\\'));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
