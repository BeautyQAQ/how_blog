package com.liushao.service;

import java.util.Optional;

import com.liushao.dao.SearchProblemDao;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author huangshen
 */
@Service
public class SearchProblemService {
    @Autowired
    private SearchProblemDao searchProblemDao;
    /**
     * 增加问题
     * @param problem
     */
    public void add(Problem problem){
        searchProblemDao.save(problem);
    }

    /**
     * 问题搜索
     * @param keywords
     * @param page
     * @param size
     * @return
     */
    public Page<Problem> findByTitleOrContentLike(String keywords, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page-1, size);
        return searchProblemDao.findByTitleOrContentLike(keywords, keywords, pageRequest);
    }

    /**
     * 问题删除
     * @param id
     */
    public Result deleteById(String id) {
        // 先查询索引中是否存在
        Optional<Problem> channel = searchProblemDao.findById(id);
        if(channel.isPresent()){
            searchProblemDao.deleteById(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }
        return new Result(true, StatusCode.OK, "删除失败，该问题不存在");
    }
}