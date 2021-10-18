package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/10/13 15:33
 */
@Data
public class adminAddGoodsForm {
	@ApiModelProperty("学院")
	@NotNull(message = "学院不能为空")
	private String college;

	@ApiModelProperty("申请时间")
	@NotNull(message = "申请时间不能为空")
	private Date applicationTime;

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

	@ApiModelProperty("货架号")
	@NotBlank(message = "货架号不能为空")
	private String shelfNumber;

	@ApiModelProperty("房间号")
	@NotBlank(message = "房间号不能为空")
	private String roomNumber;

	@ApiModelProperty("物品详情")
	@NotEmpty(message = "物品详情不能为空")
	private List<GoodsInfoForm> goodsInfoFormList;
}
