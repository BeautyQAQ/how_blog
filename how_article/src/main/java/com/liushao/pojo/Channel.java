package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 实体类
 * @author Administrator
 *
 */
@ApiModel(value = "频道")
@Entity
@Table(name="tb_channel")
public class Channel implements Serializable{

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "频道名称")
	private String name;

	@ApiModelProperty(value = "状态")
	private String state;

	
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

	public String getState() {		
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
}
