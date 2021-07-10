package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchArticleDao;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
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
     * 文章搜索--包含标题和内容
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public Page<Article> findByTitleOrContentLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return searchArticleDao.findByTitleOrContentLike(keywords, keywords, pageRequest);
    }

    /**
     * 文章标题搜索
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public Page<Article> findByTitleLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return searchArticleDao.findByTitleLike(keywords, pageRequest);
    }

    /**
     * 文章删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Article> article = searchArticleDao.findById(id);
        if(article.isPresent()){
            searchArticleDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该文章不存在");
    }

    /**
     * 文章更新
     * @param article 文章
     */
    public void update(Article article) {
        // 先查询索引中是否存在
        Optional<Article> articleOptional = searchArticleDao.findById(article.getId());
        if(articleOptional.isPresent()){
            searchArticleDao.save(article);
        }
    }
}