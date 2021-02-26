package com.liushao.controller;
import java.util.List;
import java.util.Map;

import com.liushao.entity.PageResult;
import com.liushao.entity.Result;
import com.liushao.entity.StatusCode;
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

import com.liushao.pojo.Reply;
import com.liushao.service.ReplyService;


/**
 * 控制器层
 * @author Administrator
 *
 */
@Api(tags = "回答")
@RestController
@CrossOrigin
@RequestMapping("/qa")
public class ReplyController {

	@Autowired
	private ReplyService replyService;
	
	/**
	 * 查询全部数据
	 */
	@ApiOperation(value = "查询全部回答数据")
	@RequestMapping(value = "/reply", method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true, StatusCode.OK,"查询成功",replyService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@ApiOperation(value = "根据id查询回答")
	@RequestMapping(value="/reply/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",replyService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@ApiOperation(value = "分页+条件查询")
	@RequestMapping(value="/reply/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<Reply> pageList = replyService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<Reply>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap 条件map
     * @return
     */
	@ApiOperation(value = "条件查询")
    @RequestMapping(value="/reply/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",replyService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param reply 回答
	 */
	@ApiOperation(value = "添加回答")
	@RequestMapping(value = "/reply", method=RequestMethod.POST)
	public Result add(@RequestBody Reply reply  ){
		replyService.add(reply);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param reply 回答
	 */
	@ApiOperation(value = "修改回答")
	@RequestMapping(value="/reply/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody Reply reply, @PathVariable String id ){
		reply.setId(id);
		replyService.update(reply);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id 回答id
	 */
	@ApiOperation(value = "删除回答")
	@RequestMapping(value="/reply/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		replyService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}
	
}
