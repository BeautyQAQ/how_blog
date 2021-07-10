package com.liushao.dao;

import com.liushao.pojo.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 文章数据访问层接口
 * @author huangshen
 */
public interface SearchArticleDao extends ElasticsearchRepository<Article,String> {
    /**
     * 检索标题和文章内容
     * @param
     * @return
     */
    Page<Article> findByTitleOrContentLike(String title, String content, Pageable pageable);

    /**
     * 检索标题
     * @param
     * @return
     */
    Page<Article> findByTitleLike(String title, Pageable pageable);

}
