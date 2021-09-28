package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/9/27 10:53
 */
@Data
public class UpdateAccessControlForm {
	@ApiModelProperty("负责人")
	@NotBlank(message = "负责人不能为空")
	private String chargeName;

	@ApiModelProperty("存取控制，0为可存取，1为不可存取")
	@NotNull(message = "存取控制不能为空")
	private Integer accessControl;
}
