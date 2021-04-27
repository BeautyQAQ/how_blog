package com.liushao.dao;

import com.liushao.pojo.Problem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 问题数据访问层接口
 * @author huangshen
 */
public interface SearchProblemDao extends ElasticsearchRepository<Problem,String> {

    /**
     * 检索
     */
    Page<Problem> findByTitleOrContentLike(String title, String content, Pageable pageable);

}
