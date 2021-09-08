package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 21:28
 */
@Data
public class GoodsInfoForm {
	@ApiModelProperty("物品名称")
	@NotBlank(message = "物品名称不能为空")
	private String goodsName;

	@ApiModelProperty("规格型号（L/kg）")
	@NotNull(message = "规格型号不能为空")
	@Min(0)
	private Double goodsWeight;

	@ApiModelProperty("数量")
	@NotNull(message = "数量不能为空")
	@Min(1)
	private Integer goodsNum;
}
