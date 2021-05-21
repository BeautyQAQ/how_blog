package com.liushao.client.impl;

import com.liushao.client.SearchClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Article;
import com.liushao.pojo.Channel;
import com.liushao.pojo.Column;
import com.liushao.pojo.Problem;

import org.springframework.stereotype.Component;

import aj.org.objectweb.asm.Label;

@Component
public class SearchClientImpl implements SearchClient {
    @Override
    public Result saveArticle(Article article) {
        System.out.println("saveArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveArticle熔断器启动了",article);
    }

    @Override
    public Result updateArticle(Article article) {
        System.out.println("updateArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateArticle熔断器启动了",article);
    }

    @Override
    public Result deleteArticle(String id) {
        System.out.println("deleteArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteArticle熔断器启动了",id);
    }

    @Override
    public Result saveChannel(Channel channel) {
        System.out.println("saveChannel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveChannel熔断器启动了",channel);
    }

    @Override
    public Result updateChannel(Channel channel) {
        System.out.println("updateChannel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateChannel熔断器启动了",channel);
    }

    @Override
    public Result deleteChannel(String id) {
        System.out.println("deleteChannel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteChannel熔断器启动了",id);
    }

    @Override
    public Result saveLabel(Label label) {
        System.out.println("saveLabel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveLabel熔断器启动了",label);
    }

    @Override
    public Result updateLabel(Label label) {
        System.out.println("updateLabel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateLabel熔断器启动了",label);
    }

    @Override
    public Result deleteLabel(String id) {
        System.out.println("deleteLabel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteLabel熔断器启动了",id);
    }

    @Override
    public Result saveColumn(Column column) {
        System.out.println("saveColumn熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveColumn熔断器启动了",column);
    }

    @Override
    public Result updateColumn(Column column) {
        System.out.println("updateColumn熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateColumn熔断器启动了",column);
    }

    @Override
    public Result deleteColumn(String id) {
        System.out.println("deleteColumn熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteColumn熔断器启动了",id);
    }

    @Override
    public Result saveProblem(Problem problem) {
        System.out.println("saveProblem熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveProblem熔断器启动了",problem);
    }

    @Override
    public Result updateProblem(Problem problem) {
        System.out.println("updateProblem熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateProblem熔断器启动了",problem);
    }

    @Override
    public Result deleteProblem(String id) {
        System.out.println("deleteProblem熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteProblem熔断器启动了",id);
    }

}
