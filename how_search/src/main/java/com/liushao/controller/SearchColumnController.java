package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Column;
import com.liushao.service.SearchColumnService;

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
@Api(tags = "专栏搜索")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class SearchColumnController {
    @Autowired
    private SearchColumnService searchColumnService;

    /**
     * 专栏索引添加
     * @param column
     * @return
     */
    @ApiOperation(value = "在es保存专栏")
    @RequestMapping(value = "/column", method= RequestMethod.POST)
    public Result save(@RequestBody Column column){
        searchColumnService.add(column);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 专栏搜索
     * @param keywords 关键字
     * @param page 页码
     * @param size 页面大小
     */
    @ApiOperation(value = "专栏搜索")
    @RequestMapping(value="/column/{keywords}/{page}/{size}",method= RequestMethod.GET)
    public Result findByTitleOrContentLike(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Column> columnPage = searchColumnService.findByNameOrSummaryLike(keywords, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Column>(columnPage.getTotalElements(), columnPage.getContent()));
    }

    /**
     * 删除专栏索引
     */
    @ApiOperation(value = "专栏删除")
    @RequestMapping(value="/column/delete/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id){
        return searchColumnService.deleteById(id);
    }
}
