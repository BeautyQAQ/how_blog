package com.liushao.controller;
import java.util.Map;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.liushao.pojo.Article;
import com.liushao.service.ArticleService;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
/**
 * 控制器层
 * 前缀 /article
 * @author Administrator
 *
 */
@Api(tags = "文章控制层")
@RestController
@CrossOrigin
@RequestMapping("/article")
public class ArticleController {

	@Autowired
	private ArticleService articleService;

	/**
	 * 查询全部数据
	 * @return
	 */
	@ApiOperation(value = "查询全部文章数据")
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",articleService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@ApiOperation(value = "根据id查询文章")
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",articleService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@ApiOperation(value = "分页+多条件查询")
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map<String, Object> searchMap , @PathVariable int page, @PathVariable int size){
		Page<Article> pageList = articleService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Article>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
	@ApiOperation(value = "条件查询")
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map<String, Object> searchMap){
        return new Result(true,StatusCode.OK,"查询成功",articleService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param article
	 */
	@ApiOperation(value = "增加文章")
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody Article article){
		articleService.add(article);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param article
	 */
	@ApiOperation(value = "修改文章")
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody Article article, @PathVariable String id ){
		article.setId(id);
		articleService.update(article);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@ApiOperation(value = "删除文章")
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		articleService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	/**
	 * 审核
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "审核文章")
	@RequestMapping(value="/examine/{id}",method=RequestMethod.PUT)
	public Result examine(@PathVariable String id){
		articleService.examine(id);
		return new Result(true, StatusCode.OK, "审核成功！");
	}

	/**
	 * 点赞
	 * @param id
	 * @return
	 */
	@ApiOperation(value = "文章点赞")
	@RequestMapping(value="/thumbup/{id}",method=RequestMethod.PUT)
	public Result updateThumbup(@PathVariable String id){
		articleService.updateThumbup(id);
		return new Result(true, StatusCode.OK,"点赞成功");
	}
}
