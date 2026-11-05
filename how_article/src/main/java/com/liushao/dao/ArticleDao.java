package com.liushao.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.liushao.pojo.Article;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ArticleDao extends JpaRepository<Article,String>,JpaSpecificationExecutor<Article>{
    boolean existsByImageOrContentContaining(String image, String content);
    /**
     * 审核
     * @param id
     */
    @Modifying
    @Query("update Article set state='1' where id=?1")
    void examine(String id);

    /**
     * 点赞
     * @param id
     * @return
     */
    @Modifying
    @Query("update Article a set thumbup=coalesce(thumbup,0)+1 where id=?1")
    int updateThumbup(String id);


}
