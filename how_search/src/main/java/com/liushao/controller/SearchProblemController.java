package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Problem;
import com.liushao.service.SearchProblemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 前缀 /search
 * @author huangshen
 */
@Api(tags = "问题搜索")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class SearchProblemController {
    @Autowired
    private SearchProblemService searchProblemService;

    /**
     * 问题索引添加
     * @param problem
     * @return
     */
    @ApiOperation(value = "在es保存问题")
    @RequestMapping(value = "/problem", method= RequestMethod.POST)
    public Result save(@RequestBody Problem problem){
        searchProblemService.add(problem);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 问题搜索
     * @param keywords 关键字
     * @param page 页码
     * @param size 页面大小
     */
    @ApiOperation(value = "问题搜索")
    @RequestMapping(value="/problem/{keywords}/{page}/{size}",method= RequestMethod.GET)
    public Result findByTitleOrContentLike(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Problem> problemPage = searchProblemService.findByTitleOrContentLike(keywords, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Problem>(problemPage.getTotalElements(), problemPage.getContent()));
    }

    /**
     * 删除问题索引
     */
    @ApiOperation(value = "问题删除")
    @RequestMapping(value="/problem/delete/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id){
        return searchProblemService.deleteById(id);
    }
}
