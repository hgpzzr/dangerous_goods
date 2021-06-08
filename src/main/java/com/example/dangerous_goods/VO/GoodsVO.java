package com.example.dangerous_goods.VO;

import com.example.dangerous_goods.entity.GoodsInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 13:27
 */
@Data
public class GoodsVO {
	private String goodsId;

	private String college;

	private String applicationTime;

	private String chargePhone;

	private String chargeName;

	private String agentPhone;

	private String agentName;

	private Integer overdueStatus;

	private Integer verifyStatus;

	private String shelfNumber;

	List<GoodsInfo> goodsInfoList;
}
