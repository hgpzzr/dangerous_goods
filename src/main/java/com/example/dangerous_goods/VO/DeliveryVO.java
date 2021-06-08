package com.example.dangerous_goods.VO;

import com.example.dangerous_goods.entity.OutOfStockInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 15:08
 */
@Data
public class DeliveryVO {
	private String outId;

	private String outTime;

	private Integer verifyStatus;

	private String chargeName;

	private String agentName;

	private String agentPhone;

	private String deliveryAddress;

	List<OutOfStockInfo> outOfStockInfoList;
}
