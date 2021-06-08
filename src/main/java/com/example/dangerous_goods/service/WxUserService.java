package com.example.dangerous_goods.service;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.form.WxRegisterForm;
import org.springframework.stereotype.Service;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 16:06
 */
@Service
public interface WxUserService {
	/**
	 * 录入信息
	 * @param wxRegisterForm
	 * @return
	 */
	ResultVO register(WxRegisterForm wxRegisterForm);

	/**
	 * 判断是否录入过
	 * @param openId
	 * @return
	 */
	boolean isExit(String openId);

	/**
	 * 浏览用户信息
	 * @param openId
	 * @return
	 */
	ResultVO browseUserInfo(String openId);
}
