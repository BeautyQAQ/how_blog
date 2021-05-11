package com.liushao.client;

import com.liushao.client.impl.SearchClientImpl;
import com.liushao.entity.Result;
import com.liushao.pojo.Article;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author huangshen
 */
@FeignClient(value = "how-search", fallback = SearchClientImpl.class)
public interface SearchClient {
    /**
     * 在es中新增文章
     * @param article 文章
     * @return Result
     */
    @RequestMapping(value="/search/article", method = RequestMethod.POST)
    Result saveArticle(Article article);

    /**
     * 在es中更新文章
     * @param article 文章
     * @return Result
     */
    @RequestMapping(value="/search/article/update", method = RequestMethod.PUT)
    Result updateArticle(Article article);
    /**
     * 在es中删除文章
     * @param id id
     * @return Result
     */
    @RequestMapping(value="/search/article/delete/{id}", method = RequestMethod.DELETE)
    Result deleteArticle(@PathVariable String id);

}
