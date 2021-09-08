package com.example.dangerous_goods.controller;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.form.AdminVerifyForm;
import com.example.dangerous_goods.form.DeleteForm;
import com.example.dangerous_goods.form.InsertGoodsForm;
import com.example.dangerous_goods.form.TeacherVerifyForm;
import com.example.dangerous_goods.service.GoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 21:54
 */
@RestController
@Slf4j
@RequestMapping("/api/goods")
@CrossOrigin
@Api(tags = "危险品入库接口")
public class GoodsController {
	@Autowired
	private GoodsService goodsService;

	@PostMapping("/insert")
	@ApiOperation("危险品入库申请")
	public ResultVO insertGoods(@RequestBody @Valid InsertGoodsForm insertGoodsForm){
		return goodsService.insertGoods(insertGoodsForm);
	}

	@PostMapping("/teacherVerify")
	@ApiOperation("老师入库审核")
	public ResultVO teacherVerify(@RequestBody @Valid TeacherVerifyForm teacherVerifyForm){
		return goodsService.teacherVerify(teacherVerifyForm);
	}

	@GetMapping("/browseWarehousingApplication")
	@ApiOperation("老师根据老师名字浏览需要审核的入库申请")
	public ResultVO browseWarehousingApplication(String teacherName){
		return goodsService.teacherBrowseWarehousingApplications(teacherName);
	}

	@GetMapping("/checkOverdue")
	@ApiOperation("检查有无过期")
	public ResultVO checkOverdue(){
		return goodsService.checkOverdue();
	}

	@GetMapping("/selectWarehousingApplications")
	@ApiOperation("查询老师审核过和管理员审核过的入库申请")
	public ResultVO selectWarehousingApplications(String teacherName){
		return goodsService.selectWarehousingApplications(teacherName);
	}

	@PostMapping("/delete")
	@ApiOperation("删除入库申请")
	public ResultVO delete(DeleteForm deleteForm){
		return goodsService.delete(deleteForm);
	}

	@GetMapping("/select")
	@ApiOperation("通过老师名字查询所有物品")
	public ResultVO select(String teacherName){
		return goodsService.select(teacherName);
	}

	@GetMapping("/selectByStudentName")
	@ApiOperation("通过学生名字查找入库申请")
	public ResultVO selectByStudentName(String openId){
		return goodsService.selectByStudentName(openId);
	}

	@GetMapping("/selectCollege")
	@ApiOperation("查看所有学院")
	public ResultVO selectCollege(){
		return goodsService.selectCollege();
	}

	@GetMapping("/download")
	@ApiOperation("下载word")
	public void downloadWord(HttpServletResponse response, String goodsId){
		goodsService.download(response,goodsId);
	}
}
