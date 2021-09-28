package com.example.dangerous_goods.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/9/28 10:02
 */
@Data
public class UpdateGodsForm {
	@ApiModelProperty("商品编号")
	@NotBlank(message = "商品编号不能为空")
	private String goodsId;

	@ApiModelProperty("货架号")
	@NotBlank(message = "货架号不能为空")
	private String shelfNumber;

	@ApiModelProperty("房间号")
	@NotBlank(message = "房间号不能为空")
	private String roomNumber;
}
