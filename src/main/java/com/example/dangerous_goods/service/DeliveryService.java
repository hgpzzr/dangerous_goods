package com.example.dangerous_goods.service;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.form.DeliveryForm;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 13:49
 */
@Service
public interface DeliveryService {
	/**
	 * 申请出库
	 * @param deliveryForm
	 * @return
	 */
	ResultVO applyForDelivery(DeliveryForm deliveryForm);

	/**
	 * 管理员出库审核
	 * @param outId
	 * @return
	 */
	ResultVO adminVerify(String outId);

	/**
	 * 浏览未审核的出库申请
	 * @return
	 */
	ResultVO BrowseAllNotReviewedDelivery();

	/**
	 * 生成并下载word文件
	 * @param response
	 * @param outId
	 */
	void download(HttpServletResponse response,String outId);

	/**
	 * 浏览已经审核的出库申请
	 * @param teacherName
	 * @return
	 */
	ResultVO browseReviewedDelivery(String teacherName);
}
