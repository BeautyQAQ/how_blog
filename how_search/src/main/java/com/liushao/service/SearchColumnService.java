package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchColumnDao;
import com.liushao.pojo.Column;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author huangshen
 */
@Service
public class SearchColumnService {

    @Autowired
    private SearchColumnDao searchColumnDao;

    /**
     * 增加专栏
     * 
     * @param column 专栏
     */
    public void add(Column column) {
        searchColumnDao.save(column);
    }

    /**
     * 专栏名称搜索
     * 
     * @param keywords 关键字
     * @param page 页码
     * @param size 大小
     * @return 分页结果
     */
    public Page<Column> findByNameLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return searchColumnDao.findByNameLike(keywords, pageRequest);
    }

    /**
     * 专栏简介搜索
     *
     * @param keywords 关键字
     * @param page 页码
     * @param size 大小
     * @return 分页结果
     */
    public Page<Column> findBySummaryLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return searchColumnDao.findBySummaryLike(keywords, pageRequest);
    }

    /**
     * 专栏删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Column> column = searchColumnDao.findById(id);
        if(column.isPresent()){
            searchColumnDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该专栏不存在");
    }

    /**
     * 专栏更新
     * @param column 专栏
     */
    public void update(Column column) {
        // 先查询索引中是否存在
        Optional<Column> columnOptional = searchColumnDao.findById(column.getId());
        if(columnOptional.isPresent()){
            searchColumnDao.save(column);
        }
    }
}