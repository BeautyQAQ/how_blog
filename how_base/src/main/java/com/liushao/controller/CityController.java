package com.liushao.controller;

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

import com.liushao.pojo.City;
import com.liushao.service.CityService;

/**
 * 控制器层
 * @author Administrator
 *
 */
@Api(tags = "城市模块")
@RestController
@CrossOrigin
@RequestMapping("/base/city")
public class CityController {

	@Autowired
	private CityService cityService;

	/**
	 * 查询全部数据
	 */
	@ApiOperation(value = "查询全部城市数据")
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true, StatusCode.OK,"查询成功",cityService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@ApiOperation(value = "根据id查询数据")
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",cityService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@ApiOperation(value = "分页+条件查询")
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<City> pageList = cityService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<City>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
	@ApiOperation(value = "条件查询")
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",cityService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param city
	 */
	@ApiOperation(value = "增加")
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody City city  ){
		cityService.add(city);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param city
	 */
	@ApiOperation(value = "修改")
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody City city, @PathVariable String id ){
		city.setId(id);
		cityService.update(city);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@ApiOperation(value = "删除")
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		cityService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}
	
}
