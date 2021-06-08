package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 13:51
 */
@Data
public class DeliveryForm {
	@ApiModelProperty("物品编号")
	@NotBlank(message = "物品编号不能为空")
	private String goodsId;

	@ApiModelProperty("经办人姓名")
	@NotBlank(message = "经办人姓名不能为空")
	private String agentName;

	@ApiModelProperty("经办人电话")
	@NotBlank(message = "经办人电话不能为空")
	private String agentPhone;

	@ApiModelProperty("负责人姓名")
	@NotBlank(message = "负责人姓名不能为空")
	private String chargeName;

	@ApiModelProperty("出库地址")
	@NotBlank(message = "出库地址不能为空")
	private String deliveryAddress;

	@ApiModelProperty("物品详情")
	@NotEmpty(message = "物品详情不能为空")
	private List<DeliveryInfoForm> deliveryInfoFormList;

}
