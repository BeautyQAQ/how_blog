package com.liushao.service;

import com.liushao.dao.SearchArticleDao;
import com.liushao.pojo.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author huangshen
 */
@Service
public class SearchArticleService {
    @Autowired
    private SearchArticleDao searchArticleDao;
    /**
     * 增加文章
     * @param article
     */
    public void add(Article article){
        searchArticleDao.save(article);
    }

    /**
     * 文章搜索
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public Page<Article> findByTitleLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return searchArticleDao.findByTitleOrContentLike(keywords, keywords, pageRequest);
    }
}