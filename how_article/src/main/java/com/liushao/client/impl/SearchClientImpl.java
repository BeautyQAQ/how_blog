package com.liushao.client.impl;

import com.liushao.client.SearchClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Article;
import org.springframework.stereotype.Component;

@Component
public class SearchClientImpl implements SearchClient {
    @Override
    public Result saveArticle(Article article) {
        System.out.println("saveArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveArticle熔断器启动了");
    }

    @Override
    public Result updateArticle(Article article) {
        System.out.println("updateArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateArticle熔断器启动了");
    }

    @Override
    public Result deleteArticle(String id) {
        System.out.println("deleteArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteArticle熔断器启动了");
    }

}
