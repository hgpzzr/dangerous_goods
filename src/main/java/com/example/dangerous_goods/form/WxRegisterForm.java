package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 16:09
 */
@Data
public class WxRegisterForm {
	@ApiModelProperty("微信openId")
	@NotBlank(message = "微信openId不能为空")
	private String openId;

	@ApiModelProperty("姓名")
	@NotBlank(message = "姓名不能为空")
	private String name;

	@ApiModelProperty("身份（学生或者老师）")
	@NotNull(message = "身份")
	private int type;

	@ApiModelProperty("学号")
	@NotBlank(message = "学号不能为空")
	private String stuNum;

	@ApiModelProperty("老师姓名")
	private String teacherName;
}
