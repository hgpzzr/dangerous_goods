package com.example.dangerous_goods.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.config.WxMaConfiguration;
import com.example.dangerous_goods.form.WxRegisterForm;
import com.example.dangerous_goods.service.WxUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 16:17
 */
@RestController
@Slf4j
@RequestMapping("/api/user")
@CrossOrigin
@Api(tags = "微信用户接口")
public class WxUserController {
	@Autowired
	private WxUserService wxUserService;


	@PostMapping("/register")
	@ApiOperation("微信用户信息录入")
	public ResultVO register(@Valid WxRegisterForm wxRegisterForm){
		return wxUserService.register(wxRegisterForm);
	}

	@GetMapping("/userInfo")
	@ApiOperation("查看用户信息")
	public ResultVO browseUserInfo(String openId){
		return wxUserService.browseUserInfo(openId);
	}


}
