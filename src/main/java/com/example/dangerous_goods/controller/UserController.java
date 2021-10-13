package com.example.dangerous_goods.controller;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.form.*;
import com.example.dangerous_goods.service.DeliveryService;
import com.example.dangerous_goods.service.GoodsService;
import com.example.dangerous_goods.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 11:01
 */
@RestController
@Slf4j
@RequestMapping("/api/admin")
@CrossOrigin
@Api(tags = "web用户接口")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private DeliveryService deliveryService;

	@PostMapping("/login")
	@ApiOperation("登录")
	public ResultVO login(@RequestBody @Valid LoginForm loginForm, HttpServletResponse response){
		return userService.login(loginForm,response);
	}

	@PostMapping("/register")
	@ApiOperation("注册")
	public ResultVO register(@RequestBody @Valid RegisterForm registerForm){
		return userService.register(registerForm);
	}

	@GetMapping("/browseWarehousingApplication")
	@ApiOperation("管理员查看老师审核过的入库申请")
	public ResultVO browseGoods(){
		return goodsService.adminBrowseAllWarehousingApplications();
	}

	@PostMapping("/Verify")
	@ApiOperation("管理员入库审核")
	public ResultVO adminVerify(@RequestBody @Valid AdminVerifyForm adminVerifyForm){
		return goodsService.adminVerify(adminVerifyForm);
	}

	@PostMapping("/adminVerify")
	@ApiOperation("管理员出库审核")
	public ResultVO adminVerify(String outId){
		return deliveryService.adminVerify(outId);
	}

	@GetMapping("/exportExcel")
	@ApiOperation("生成excel")
	public void exportExcel(HttpServletResponse response){
		goodsService.exportExcel(response);
	}

	@PostMapping("importExcel")
	@ApiOperation("从excel中批量导入数据")
	public ResultVO importExcel(@RequestParam("file") MultipartFile file){
		return goodsService.importExcel(file);
	}

	@GetMapping("/selectOverdue")
	@ApiOperation("查询超期的物品")
	public ResultVO selectOverdue(){
		return goodsService.selectOverdueGoods();
	}

	@PostMapping("/extension")
	@ApiOperation("延期")
	public ResultVO extension(ExtensionForm extensionForm){
		return goodsService.extension(extensionForm);
	}

	@PostMapping("/addTeacher")
	@ApiOperation("从excel中批量导入教师信息")
	public ResultVO addTeacher(@RequestParam("file") MultipartFile file){
		return userService.addTeacher(file);
	}

	@PostMapping("/addGoods")
	@ApiOperation("管理员添加库存")
	public ResultVO addGoods(adminAddGoodsForm form){
		return goodsService.adminAddGoods(form);
	}

}