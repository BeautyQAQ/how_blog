package com.liushao.client.impl;

import com.liushao.client.SearchClient;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
import com.liushao.pojo.Article;
import com.liushao.pojo.Channel;
import com.liushao.pojo.Column;
import com.liushao.pojo.Label;
import com.liushao.pojo.Problem;
import com.liushao.pojo.Reply;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SearchClientImpl implements SearchClient {
    @Override
    public Result saveArticle(Article article) {
        log.info("saveArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveArticle熔断器启动了",article);
    }

    @Override
    public Result updateArticle(Article article) {
        log.info("updateArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateArticle熔断器启动了",article);
    }

    @Override
    public Result deleteArticle(String id) {
        log.info("deleteArticle熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteArticle熔断器启动了",id);
    }

    @Override
    public Result saveChannel(Channel channel) {
        log.info("saveChannel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveChannel熔断器启动了",channel);
    }

    @Override
    public Result updateChannel(Channel channel) {
        log.info("updateChannel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateChannel熔断器启动了",channel);
    }

    @Override
    public Result deleteChannel(String id) {
        log.info("deleteChannel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteChannel熔断器启动了",id);
    }

    @Override
    public Result saveLabel(Label label) {
        log.info("saveLabel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveLabel熔断器启动了",label);
    }

    @Override
    public Result updateLabel(Label label) {
        log.info("updateLabel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateLabel熔断器启动了",label);
    }

    @Override
    public Result deleteLabel(String id) {
        log.info("deleteLabel熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteLabel熔断器启动了",id);
    }

    @Override
    public Result saveColumn(Column column) {
        log.info("saveColumn熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveColumn熔断器启动了",column);
    }

    @Override
    public Result updateColumn(Column column) {
        log.info("updateColumn熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateColumn熔断器启动了",column);
    }

    @Override
    public Result deleteColumn(String id) {
        log.info("deleteColumn熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteColumn熔断器启动了",id);
    }

    @Override
    public Result saveProblem(Problem problem) {
        log.info("saveProblem熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveProblem熔断器启动了",problem);
    }

    @Override
    public Result updateProblem(Problem problem) {
        log.info("updateProblem熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateProblem熔断器启动了",problem);
    }

    @Override
    public Result deleteProblem(String id) {
        log.info("deleteProblem熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteProblem熔断器启动了",id);
    }

    @Override
    public Result saveReply(Reply reply) {
        log.info("saveReply熔断器启动了");
        return new Result(false, StatusCode.ERROR,"saveReply熔断器启动了",reply);
    }

    @Override
    public Result updateReply(Reply reply) {
        log.info("updateReply熔断器启动了");
        return new Result(false, StatusCode.ERROR,"updateReply熔断器启动了",reply);
    }

    @Override
    public Result deleteReply(String id) {
        log.info("deleteReply熔断器启动了");
        return new Result(false, StatusCode.ERROR,"deleteReply熔断器启动了",id);
    }

}
