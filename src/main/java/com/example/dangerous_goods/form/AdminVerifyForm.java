package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 13:03
 */
@Data
public class AdminVerifyForm {
	@ApiModelProperty("物品编号")
	@NotBlank(message = "物品编号不能为空")
	private String goodsId;

	@ApiModelProperty("货架号")
	@NotBlank(message = "货架号不能为空")
	private String shelfNumber;
}