package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Label;
import com.liushao.service.SearchLabelService;

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
@Api(tags = "标签搜索")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class SearchLabelController {
    @Autowired
    private SearchLabelService searchLabelService;

    /**
     * 标签索引添加
     * @param label
     * @return
     */
    @ApiOperation(value = "在es保存标签")
    @RequestMapping(value = "/label", method= RequestMethod.POST)
    public Result save(@RequestBody Label label){
        searchLabelService.add(label);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 标签搜索
     * @param keywords 关键字
     * @param page 页码
     * @param size 页面大小
     */
    @ApiOperation(value = "标签搜索")
    @RequestMapping(value="/label/{keywords}/{page}/{size}",method= RequestMethod.GET)
    public Result findByLabelnameLike(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Label> labelPage = searchLabelService.findByLabelnameLike(keywords, page, size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(labelPage.getTotalElements(), labelPage.getContent()));
    }

    /**
     * 删除标签索引
     */
    @ApiOperation(value = "标签删除")
    @RequestMapping(value="/label/delete/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id){
        return searchLabelService.deleteById(id);
    }

    /**
     * 更新标签索引
     */
    @ApiOperation(value = "标签更新")
    @RequestMapping(value="/label/update",method= RequestMethod.PUT)
    public Result update(Label label){
        searchLabelService.update(label);
        return new Result(true, StatusCode.OK, "操作成功");
    }
}
