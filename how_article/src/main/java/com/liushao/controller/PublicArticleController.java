package com.liushao.controller;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.service.ArticleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article/public")
@Tag(name = "公开文章")
public class PublicArticleController {
    private final ArticleService articles;
    public PublicArticleController(ArticleService articles) { this.articles = articles; }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String channelid, @RequestParam(required = false) String keyword) {
        var result = articles.findPublic(page, size, channelid, keyword);
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<>(result.getTotalElements(), result.getContent()));
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", articles.findPublicById(id));
    }
}
