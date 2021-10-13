package com.example.dangerous_goods.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/2 11:39
 */
@Data
public class ExcelForm {
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

	private int overdueStatus;

	private int verifyStatus;

	private int takeOutStatus;

	private String goodsInfoId;

	@ExcelProperty("物品名称")
	private String goodsName;

	@ExcelProperty("规格型号（L/kg）")
	private double goodsWeight;

	@ExcelProperty("数量(桶/瓶）")
	private int goodsNum;

	@ExcelProperty("房间号")
	private String roomNumber;
}
