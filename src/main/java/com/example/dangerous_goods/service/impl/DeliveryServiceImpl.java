package com.example.dangerous_goods.service.impl;

import com.example.dangerous_goods.VO.DeliveryVO;
import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.dao.GoodsInfoMapper;
import com.example.dangerous_goods.dao.GoodsMapper;
import com.example.dangerous_goods.dao.OutOfStockInfoMapper;
import com.example.dangerous_goods.dao.OutOfStockMapper;
import com.example.dangerous_goods.entity.Goods;
import com.example.dangerous_goods.entity.GoodsInfo;
import com.example.dangerous_goods.entity.OutOfStock;
import com.example.dangerous_goods.entity.OutOfStockInfo;
import com.example.dangerous_goods.enums.ResultEnum;
import com.example.dangerous_goods.form.DeliveryForm;
import com.example.dangerous_goods.form.DeliveryInfoForm;
import com.example.dangerous_goods.service.DeliveryService;
import com.example.dangerous_goods.utils.FileUtil;
import com.example.dangerous_goods.utils.GenerateIdUtil;
import com.example.dangerous_goods.utils.ResultVOUtil;
import com.github.qrpcode.domain.WordGo;
import com.github.qrpcode.domain.WordTable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.lang.management.BufferPoolMXBean;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 13:56
 */
@Data
@Service
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {
	@Autowired
	private OutOfStockMapper outOfStockMapper;
	@Autowired
	private OutOfStockInfoMapper outOfStockInfoMapper;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private GoodsInfoMapper goodsInfoMapper;
	@Autowired
	private GenerateIdUtil generateIdUtil;

	@Value("${word.filePath}")
	private String filePath;
	@Value("${word.outTemplatePath}")
	private String outTemplatePath;
	@Value("${img.url}")
	private String imgUrl;

	@Transactional
	@Override
	public ResultVO applyForDelivery(DeliveryForm deliveryForm) {
		// ??????????????????
		for (DeliveryInfoForm deliveryInfoForm : deliveryForm.getDeliveryInfoFormList()) {
			if (deliveryInfoForm.getGoodsNum() < 1) {
				return ResultVOUtil.error(ResultEnum.DIGITAL_SPECIFICATION_ERROR);
			}
		}
		// ?????????????????????????????????
		Goods goods = goodsMapper.selectByPrimaryKey(deliveryForm.getGoodsId());
		if (goods == null) {
			return ResultVOUtil.error(ResultEnum.GOODS_NOT_EXIST_ERROR);
		}
		List<Goods> goodsList = goodsMapper.selectByChargeNameAndTakeOutStatus(goods.getChargeName());
		for (Goods goods1 : goodsList) {
			if (goods1.getAccessControl() == 1) {
				return ResultVOUtil.error(ResultEnum.OVERDUE_ERROR);
			}
		}
		// ??????????????????????????????????????????
		for (DeliveryInfoForm deliveryInfoForm : deliveryForm.getDeliveryInfoFormList()) {
			GoodsInfo goodsInfo = goodsInfoMapper.selectByGoodsIdAndGoodsName(goods.getGoodsId(), deliveryInfoForm.getGoodsName());
			if (goodsInfo.getGoodsNum() < deliveryInfoForm.getGoodsNum()) {
				return ResultVOUtil.error(ResultEnum.UNDER_STOCK_ERROR);
			}
		}
		// ??????????????????????????????
		OutOfStock outOfStock = new OutOfStock();
		BeanUtils.copyProperties(deliveryForm, outOfStock);
		// ???????????????outId
		String outId = generateIdUtil.getRandomId(outOfStockMapper);
		outOfStock.setOutId(outId);
		// ??????????????????
		Date date = new Date();
		outOfStock.setOutTime(date);
		// ??????????????????
		outOfStock.setVerifyStatus(0);
		int insert = outOfStockMapper.insert(outOfStock);
		if (insert != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		// ??????????????????????????????
		OutOfStockInfo outOfStockInfo = new OutOfStockInfo();
		outOfStockInfo.setOutId(outId);
		for (DeliveryInfoForm deliveryInfoForm : deliveryForm.getDeliveryInfoFormList()) {
			BeanUtils.copyProperties(deliveryInfoForm, outOfStockInfo);
			// ???????????????outInfoId
			String outInfoId = generateIdUtil.getRandomId(outOfStockInfoMapper);
			outOfStockInfo.setOutInfoId(outInfoId);
			// ???????????????
			outOfStockInfoMapper.insert(outOfStockInfo);
		}
		return ResultVOUtil.success("????????????");
	}

	@Transactional
	@Override
	public ResultVO adminVerify(String outId) {
		OutOfStock outOfStock = outOfStockMapper.selectByPrimaryKey(outId);
		// ??????????????????
		outOfStock.setVerifyStatus(1);
		int update = outOfStockMapper.updateByPrimaryKey(outOfStock);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		// ????????????
		List<OutOfStockInfo> outOfStockInfoList = outOfStockInfoMapper.selectByOutId(outId);
		for (OutOfStockInfo outOfStockInfo : outOfStockInfoList) {
			GoodsInfo goodsInfo = goodsInfoMapper.selectByGoodsIdAndGoodsName(outOfStock.getGoodsId(), outOfStockInfo.getGoodsName());
			// ????????????
			goodsInfo.setGoodsNum(goodsInfo.getGoodsNum() - outOfStockInfo.getGoodsNum());
			// ???????????????
			int update1 = goodsInfoMapper.updateByPrimaryKey(goodsInfo);
			if (update1 != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		// ????????????????????????
		List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(outOfStock.getGoodsId());
		int flag = 0;
		for (GoodsInfo goodsInfo : goodsInfoList) {
			if (goodsInfo.getGoodsNum() != 0) {
				flag = 1;
				break;
			} else {
				goodsInfo.setTakeOutStatus(1);
				goodsInfoMapper.updateByPrimaryKey(goodsInfo);
			}
		}
		// ???????????????0 ??????????????? ?????????????????????1
		if (flag == 0) {
//			// ??????????????????
//			goodsInfoMapper.deleteByGoodsId(outOfStock.getGoodsId());
//			// ????????????
//			int delete = goodsMapper.deleteByPrimaryKey(outOfStock.getGoodsId());
//			if (delete != 1) {
//				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
//			}
			Goods goods = goodsMapper.selectByPrimaryKey(outOfStock.getGoodsId());
			goods.setTakeOutStatus(1);
			int update1 = goodsMapper.updateByPrimaryKey(goods);
			if (update1 != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO BrowseAllNotReviewedDelivery() {
		List<DeliveryVO> deliveryVOList = new ArrayList<>();
		List<OutOfStock> outOfStocks = outOfStockMapper.selectByVerifyStatus(0);
		for (OutOfStock outOfStock : outOfStocks) {
			log.info("outOfStock:{}", outOfStock.toString());
			DeliveryVO deliveryVO = new DeliveryVO();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			BeanUtils.copyProperties(outOfStock, deliveryVO);
			deliveryVO.setOutTime(simpleDateFormat.format(outOfStock.getOutTime()));
			deliveryVO.setChargeName(goodsMapper.selectByPrimaryKey(outOfStock.getGoodsId()).getChargeName());
			List<OutOfStockInfo> outOfStockInfoList = outOfStockInfoMapper.selectByOutId(outOfStock.getOutId());
			deliveryVO.setOutOfStockInfoList(outOfStockInfoList);
			deliveryVOList.add(deliveryVO);
		}
		return ResultVOUtil.success(deliveryVOList);
	}

	@Override
	public void download(HttpServletResponse response, String outId) {
		// ????????????
		OutOfStock outOfStock = outOfStockMapper.selectByPrimaryKey(outId);
		// ??????????????????
		if (outOfStock.getVerifyStatus() != 1) {
			response.setHeader("state", "1");
//			throw new RuntimeException("?????????????????????????????????");
			return;
		}
		// ??????????????????
		List<OutOfStockInfo> outOfStockInfoList = outOfStockInfoMapper.selectByOutId(outId);
		// ??????
		Goods goods = goodsMapper.selectByPrimaryKey(outOfStock.getGoodsId());
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
//		WordGo wordGo = new WordGo();
//		log.info("wordGo???{}",wordGo.toString());
//		wordGo.addLine("???????????????", "font-size:??????;font-width:bold;text-align:center");
//
//		WordTable wordTable1 = new WordTable(5, 4,"row-height:1=5%,2=5%,3=5%,4=5%,5=%5");
//		wordTable1.add(1, 1, "??????????????????",goods.getCollege());
//		wordTable1.add(1, 3, "????????????", sd.format(goods.getApplicationTime()));
//		wordTable1.add(2, 1, "?????????????????????", goods.getChargePhone());
//		wordTable1.add(2, 3, "?????????????????????", goods.getChargeName());
//		wordTable1.add(3, 1, "???????????????", outOfStock.getAgentPhone());
//		wordTable1.add(3, 3, "???????????????", outOfStock.getAgentName());
//		wordTable1.add(4, 1, "????????????", sd.format(goods.getApplicationTime()));
//		wordTable1.add(4, 3, "????????????", sd.format(outOfStock.getOutTime()));
//		wordTable1.add(5, 1, "????????????", outOfStock.getDeliveryAddress());
//		log.info("wordTable1:{}",wordTable1.toString());
//		wordGo.addTable(wordTable1);
//		WordTable wordTable = new WordTable(outOfStockInfoList.size() + 1, 3,"row-height:1=5%,2=5%,3=5%,4=5%");
//		wordTable.add(1, 1, "????????????", "???????????????L/kg???", "??????(???/??????");
//		for (int i = 0; i < outOfStockInfoList.size(); i++) {
//			OutOfStockInfo outOfStockInfo = outOfStockInfoList.get(i);
//			wordTable.add(i + 2, 1, outOfStockInfo.getGoodsName(), outOfStockInfo.getGoodsWeight().toString(), outOfStockInfo.getGoodsNum().toString());
//		}
//		log.info("wordTable:{}",wordTable.toString());
//		wordGo.addTable(wordTable);
//
//		wordGo.addImg(imgUrl, " new-line:true; position: absolute; left: 10px; top:140px; width:350px; height:300px");
//		log.info("filePath:{}",filePath+outId+".docx");
//		wordGo.create(filePath + outId + ".docx");
		// ??????????????????
		String templatePath = outTemplatePath;
		// ????????????
		String outPath = filePath + outOfStock.getOutId() + ".docx";
		log.info("->>>>>>>>>>>>>>>>>>outPath:{}",outPath);
		Map<String, Object> paramMap = new HashMap<>(16);
		// ???????????????????????? ?????????????????? {str,str}
		log.info("goods:{}",goods);
		paramMap.put("college", goods.getCollege());
		paramMap.put("applicationTime", sd.format(outOfStock.getOutTime()));
		paramMap.put("chargePhone", goods.getChargePhone());
		paramMap.put("chargeName", goods.getChargePhone());
		paramMap.put("agentPhone", outOfStock.getAgentPhone());
		paramMap.put("agentName", outOfStock.getAgentName());
		paramMap.put("inTime", sd.format(goods.getApplicationTime()));
		paramMap.put("outTime", sd.format(outOfStock.getOutTime()));
		paramMap.put("deliveryAddress", outOfStock.getDeliveryAddress());
		for (OutOfStockInfo outOfStockInfo : outOfStockInfoList) {
			paramMap.put("goodsName", outOfStockInfo.getGoodsName());
			paramMap.put("weight", outOfStockInfo.getGoodsWeight());
			paramMap.put("number", outOfStockInfo.getGoodsNum());
		}
		com.example.dangerous_goods.utils.DynWordUtils.process(paramMap, templatePath, outPath);
		FileUtil.downloadFile(response, filePath + outId + ".docx");
//		FileUtil.deleteFile(filePath + outId + ".docx");
	}

	@Override
	public ResultVO browseReviewedDelivery(String teacherName) {
		List<DeliveryVO> deliveryVOList = new ArrayList<>();
		List<OutOfStock> outOfStocks = outOfStockMapper.selectByTeacherNameAndVerifyStatus(1, teacherName);
		for (OutOfStock outOfStock : outOfStocks) {
			DeliveryVO deliveryVO = new DeliveryVO();
			BeanUtils.copyProperties(outOfStock, deliveryVO);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			deliveryVO.setOutTime(simpleDateFormat.format(outOfStock.getOutTime()));
			deliveryVO.setChargeName(outOfStock.getChargeName());
			List<OutOfStockInfo> outOfStockInfoList = outOfStockInfoMapper.selectByOutId(outOfStock.getOutId());
			deliveryVO.setOutOfStockInfoList(outOfStockInfoList);
			deliveryVOList.add(deliveryVO);
		}
		return ResultVOUtil.success(deliveryVOList);
	}

	@Override
	public ResultVO selectItemsUnderReview(String goodsId, String goodsName) {
		if ("".equals(goodsId) || "".equals(goodsName)) {
			return ResultVOUtil.error(ResultEnum.PARAM_NULL_ERROR);
		}
		int sum = 0;
		List<OutOfStock> outOfStockList = outOfStockMapper.selectByGoodsId(goodsId);
		for (OutOfStock outOfStock : outOfStockList) {
			if (outOfStock.getVerifyStatus() == 0) {
				List<OutOfStockInfo> outOfStockInfoList = outOfStockInfoMapper.selectByOutId(outOfStock.getOutId());
				for (OutOfStockInfo outOfStockInfo : outOfStockInfoList) {
					if (goodsName.equals(outOfStockInfo.getGoodsName())) {
						sum += outOfStockInfo.getGoodsNum();
					}
				}
			}
		}
		List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goodsId);
		for (GoodsInfo goodsInfo : goodsInfoList){
			if(goodsName.equals(goodsInfo.getGoodsName())){
				sum = goodsInfo.getGoodsNum() - sum;
				break;
			}
		}
			return ResultVOUtil.success(sum);
	}

	@Override
	public ResultVO adminRefuse(String outId) {
		if ("".equals(outId)) {
			return ResultVOUtil.error(ResultEnum.PARAM_NULL_ERROR);
		}
		OutOfStock outOfStock = outOfStockMapper.selectByPrimaryKey(outId);
		outOfStock.setVerifyStatus(2);
		int update = outOfStockMapper.updateByPrimaryKey(outOfStock);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}
}