package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Article;
import com.liushao.service.SearchArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 前缀 /search
 * @author huangshen
 */
@Api(tags = "文章搜索")
@RestController
@CrossOrigin
@RequestMapping("/search")
public class SearchArticleController {
    @Autowired
    private SearchArticleService searchArticleService;

    /**
     * 文章索引添加
     */
    @ApiOperation(value = "在es保存文章")
    @RequestMapping(value = "/article", method= RequestMethod.POST)
    public Result save(@RequestBody Article article){
        searchArticleService.add(article);
        return new Result(true, StatusCode.OK, "操作成功");
    }

    /**
     * 文章搜索
     * @param keywords 关键字
     * @param page 页码
     * @param size 页面大小
     */
    @ApiOperation(value = "文章搜索")
    @RequestMapping(value="/article/{keywords}/{page}/{size}",method= RequestMethod.GET)
    public Result findByTitleOrContentLike(@PathVariable String keywords, @PathVariable int page, @PathVariable int size){
        Page<Article> articlePage = searchArticleService.findByTitleOrContentLike(keywords,page,size);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Article>(articlePage.getTotalElements(), articlePage.getContent()));
    }

    /**
     * 删除文章索引
     */
    @ApiOperation(value = "专栏删除")
    @RequestMapping(value="/article/delete/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id){
        return searchArticleService.deleteById(id);
    }
}
