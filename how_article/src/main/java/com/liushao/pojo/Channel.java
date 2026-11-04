package com.liushao.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
/**
 * 频道实体类
 * @author Administrator
 *
 */
@Data
@Schema(description = "频道")
@Entity
@Table(name="tb_channel")
public class Channel implements Serializable{

	private static final long serialVersionUID = 1L;

	@Schema(description = "ID")
	@Id
	private String id;

	@Schema(description = "频道名称", requiredMode = Schema.RequiredMode.REQUIRED)
	private String name;

	@Schema(description = "状态")
	private String state;
	
}
