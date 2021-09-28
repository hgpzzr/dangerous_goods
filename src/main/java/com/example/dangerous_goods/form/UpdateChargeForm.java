package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/9/28 14:48
 */
@Data
public class UpdateChargeForm {
	@ApiModelProperty("商品编号")
	@NotBlank(message = "商品编号不能为空")
	private String goodsId;

	@ApiModelProperty("负责人姓名")
	@NotBlank(message = "负责人姓名不能为空")
	private String chargePhone;

	@ApiModelProperty("负责人电话")
	@NotBlank(message = "负责人电话不能为空")
	private String chargeName;

	@ApiModelProperty("学院名称")
	@NotBlank(message = "学院名称不能为空")
	private String college;
}
