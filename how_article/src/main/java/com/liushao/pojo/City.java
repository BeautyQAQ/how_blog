package com.liushao.pojo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
/**
 * 城市实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "城市")
@Entity
@Table(name="tb_city")
public class City implements Serializable{

	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = 7638885999116727655L;

	@Schema(description = "ID")
	@Id
	private String id;

	@Schema(description = "城市名称", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	@Schema(description = "是否热门")
	private String ishot;
}
