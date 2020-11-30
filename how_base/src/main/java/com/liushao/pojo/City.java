package com.liushao.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 城市实体类
 * @author Administrator
 *
 */
@ApiModel(value = "城市")
@Entity
@Table(name="tb_city")
public class City implements Serializable{

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "城市名称")
	private String name;

	@ApiModelProperty(value = "是否热门")
	private String ishot;

	
	public String getId() {		
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {		
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getIshot() {		
		return ishot;
	}
	public void setIshot(String ishot) {
		this.ishot = ishot;
	}
}
