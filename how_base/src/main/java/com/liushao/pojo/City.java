package com.liushao.pojo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 城市实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "城市")
@Entity
@Table(name="tb_city")
public class City implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 7638885999116727655L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "城市名称", required = true)
	private String name;

	@ApiModelProperty(value = "是否热门")
	private String ishot;
}
