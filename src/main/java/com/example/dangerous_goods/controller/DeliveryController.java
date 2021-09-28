package com.example.dangerous_goods.controller;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.form.DeliveryForm;
import com.example.dangerous_goods.service.DeliveryService;
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
 * @date 2021/6/1 14:28
 */
@RestController
@Slf4j
@RequestMapping("/api/delivery")
@CrossOrigin
@Api(tags = "出库接口")
public class DeliveryController {
	@Autowired
	private DeliveryService deliveryService;
	@Autowired
	private GoodsService goodsService;

	@GetMapping("/browseGoods")
	@ApiOperation("根据老师名字浏览未出库的危险品")
	public ResultVO browseGoods(String teacherName){
		return goodsService.browseGoods(teacherName);
	}

	@PostMapping("/insert")
	@ApiOperation("出库申请")
	public ResultVO insert(@RequestBody @Valid DeliveryForm deliveryForm){
		return deliveryService.applyForDelivery(deliveryForm);
	}



	@GetMapping("/browse")
	@ApiOperation("查询所有待审核的出库申请")
	public ResultVO BrowseAllNotReviewedDelivery(){
		return deliveryService.BrowseAllNotReviewedDelivery();
	}

	@GetMapping("/download")
	@ApiOperation("下载word")
	public void downloadWord(HttpServletResponse response,String outId){
		deliveryService.download(response,outId);
	}

	@GetMapping("/reviewed")
	@ApiOperation("根据老师名字查询已通过审核的出库申请")
	public ResultVO browseReviewedDelivery(String teacherName){
		return deliveryService.browseReviewedDelivery(teacherName);
	}

	@GetMapping("/numberToBeReviewed")
	@ApiOperation("查询待审核的物品的数量")
	public ResultVO selectItemsUnderReview(String goodsId,String goodsName){
		return deliveryService.selectItemsUnderReview(goodsId,goodsName);
	}

	@PostMapping("/adminRefuse")
	@ApiOperation("管理员拒绝出库申请")
	public ResultVO adminRefuse(String outId){
		return deliveryService.adminRefuse(outId);
	}
}
