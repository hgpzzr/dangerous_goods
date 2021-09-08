package com.example.dangerous_goods.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.config.WxMaConfiguration;
import com.example.dangerous_goods.service.WxUserService;
import com.example.dangerous_goods.utils.JsonUtils;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 17:11
 */
@RestController
@RequestMapping("/api/wx/user/")
public class WxMaUserController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private WxUserService wxUserService;

	@Value("${appid}")
	private String appid;

	/**
	 * 登陆接口
	 */
	@GetMapping("/login")
	public String login(String code) {
		if (StringUtils.isBlank(code)) {
			return "empty jscode";
		}

		final WxMaService wxService = WxMaConfiguration.getMaService(appid);

		try {
			WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
			this.logger.info(session.getSessionKey());
			this.logger.info(session.getOpenid());
			boolean exit = wxUserService.isExit(session.getOpenid());
			Map<String, Serializable> map = new HashMap();
			map.put("session",session);
			map.put("status",exit);
			return JsonUtils.toJson(map);
		} catch (WxErrorException e) {
			this.logger.error(e.getMessage(), e);
			return e.toString();
		}
	}

	/**
	 * <pre>
	 * 获取用户信息接口
	 * </pre>
	 */
	@GetMapping("/info")
	public String info(String sessionKey,
					   String signature, String rawData, String encryptedData, String iv) {
		final WxMaService wxService = WxMaConfiguration.getMaService(appid);

		// 用户信息校验
		if (!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			return "user check failed";
		}

		// 解密用户信息
		WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

		return JsonUtils.toJson(userInfo);
	}

}
