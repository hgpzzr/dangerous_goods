package com.example.dangerous_goods.form;

import com.example.dangerous_goods.entity.GoodsInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 21:23
 */
@Data
public class InsertGoodsForm {
	@ApiModelProperty("学院")
	@NotNull(message = "学院不能为空")
	private int college;

	@ApiModelProperty("负责人电话")
	@NotBlank(message = "负责人电话不能为空")
	private String chargePhone;

	@ApiModelProperty("负责人姓名")
	@NotBlank(message = "负责人姓名不能为空")
	private String chargeName;

	@ApiModelProperty("经办人电话")
	@NotBlank(message = "经办人电话不能为空")
	private String agentPhone;

	@ApiModelProperty("经办人姓名")
	@NotBlank(message = "经办人姓名不能为空")
	private String agentName;

	@ApiModelProperty("物品详情")
	@NotEmpty(message = "物品详情不能为空")
	private List<GoodsInfoForm> goodsInfoFormList;
}
