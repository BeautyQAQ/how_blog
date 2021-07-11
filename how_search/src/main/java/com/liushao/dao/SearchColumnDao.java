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
     * 专栏名称搜索
     */
    Page<Column> findByNameLike(String name, Pageable pageable);

    /**
     * 专栏简介搜索
     */
    Page<Column> findBySummaryLike(String summary, Pageable pageable);
}
