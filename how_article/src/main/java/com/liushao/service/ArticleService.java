package com.liushao.service;

import com.liushao.client.SearchClient;
import com.liushao.dao.ArticleDao;
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
    @Autowired private SearchClient searchClient;
    @Autowired private IdWorker idWorker;
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

    public Page<Article> findPublic(int page, int size, String channelid, String keyword) {
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
        return articleDao.findAll(spec, PageRequest.of(page - 1, size,
                Sort.by(Sort.Direction.DESC, "istop", "updatetime", "id")));
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
        article.setId(String.valueOf(idWorker.nextId()));
        article.setCreatetime(new Date());
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
        article.setTitle(input.getTitle());
        article.setContent(input.getContent());
        article.setImage(input.getImage());
        article.setChannelid(input.getChannelid());
        article.setColumnid(input.getColumnid());
        article.setUrl(input.getUrl());
        article.setType(input.getType());
        article.setIspublic(flag(input.getIspublic(), article.getIspublic()));
        article.setIstop(flag(input.getIstop(), article.getIstop()));
        article.setUpdatetime(new Date());
        articleDao.saveAndFlush(article);
        if ("1".equals(article.getState())) syncIndex(article);
        invalidateAfterCommit(article.getId());
    }

    @Transactional
    public void deleteById(String id) {
        Article article = requireArticle(id);
        if ("1".equals(article.getState())) requireSearchSuccess(searchClient.deleteArticle(id));
        articleDao.deleteById(id);
        invalidateAfterCommit(id);
    }

    @Transactional
    public void examine(String id) {
        Article article = requireArticle(id);
        article.setState("1");
        article.setUpdatetime(new Date());
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
        article.setTitle(article.getTitle().trim());
    }

    private String flag(String value, String fallback) {
        if (value == null) return fallback == null ? "0" : fallback;
        if (!"0".equals(value) && !"1".equals(value)) throw new IllegalArgumentException("状态只能是0或1");
        return value;
    }

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
