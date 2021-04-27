package com.liushao.dao;

import com.liushao.pojo.Column;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 专栏数据访问层接口
 * @author huangshen
 */
public interface SearchColumnDao extends ElasticsearchRepository<Column,String> {

    /**
     * 检索
     */
    Page<Column> findByNameOrSummaryLike(String name, String summary, Pageable pageable);

}
