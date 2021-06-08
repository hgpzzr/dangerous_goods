package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 13:52
 */
@Data
public class DeliveryInfoForm {
	@ApiModelProperty("物品名称")
	@NotBlank(message = "物品名称不能为空")
	private String goodsName;

	@ApiModelProperty("物品数量")
	@NotNull(message = "物品数量不能为空")
	private int goodsNum;

	@ApiModelProperty("物品规格")
	@NotNull(message = "物品规格不能为空")
	private double goodsWeight;
}
