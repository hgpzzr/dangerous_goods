package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 10:22
 */
@Data
public class TeacherVerifyForm {
	@ApiModelProperty("物品编号")
	@NotBlank(message = "物品编号不能为空")
	private String goodsId;

	@ApiModelProperty("微信openId")
	@NotBlank(message = "微信openId不能为空")
	private String openId;
}
