package com.liushao.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
/**
 * 频道实体类
 * @author Administrator
 *
 */
@Data
@ApiModel(value = "频道")
@Entity
@Table(name="tb_channel")
public class Channel implements Serializable{

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "ID")
	@Id
	private String id;

	@ApiModelProperty(value = "频道名称", required = true)
	private String name;

	@ApiModelProperty(value = "状态")
	private String state;
	
}
