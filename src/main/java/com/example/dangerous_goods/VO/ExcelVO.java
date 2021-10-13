package com.example.dangerous_goods.VO;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/2 10:38
 */
@Data
public class ExcelVO {
	@ExcelProperty("物品编号")
	private String goodsId;

	@ExcelProperty("学院（中心）")
	private String college;

	@ExcelProperty("申请时间")
	private String applicationTime;

	@ExcelProperty("物品负责人姓名")
	private String chargeName;

	@ExcelProperty("物品负责人电话")
	private String chargePhone;

	@ExcelProperty("经办人姓名")
	private String agentName;

	@ExcelProperty("经办人电话")
	private String agentPhone;

	@ExcelProperty("货架号")
	private String shelfNumber;

	@ExcelProperty("房间号")
	private String roomNumber;

	@ExcelProperty("物品名称")
	private String goodsName;

	@ExcelProperty("规格型号（L/kg）")
	private double goodsWeight;

	@ExcelProperty("数量(桶/瓶）")
	private int goodsNum;
}
