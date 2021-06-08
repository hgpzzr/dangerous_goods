package com.example.dangerous_goods.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.example.dangerous_goods.VO.*;
import com.example.dangerous_goods.dao.*;
import com.example.dangerous_goods.entity.*;
import com.example.dangerous_goods.enums.ResultEnum;
import com.example.dangerous_goods.form.*;
import com.example.dangerous_goods.listener.GoodsListener;
import com.example.dangerous_goods.service.GoodsService;
import com.example.dangerous_goods.utils.FileUtil;
import com.example.dangerous_goods.utils.GenerateIdUtil;
import com.example.dangerous_goods.utils.ResultVOUtil;

import com.github.qrpcode.domain.WordGo;
import com.github.qrpcode.domain.WordTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.util.resources.cldr.kk.CalendarData_kk_Cyrl_KZ;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 21:22
 */
@Service
@Slf4j
@EnableScheduling
public class GoodsServiceImpl implements GoodsService {
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private GoodsInfoMapper goodsInfoMapper;
	@Autowired
	private GenerateIdUtil generateIdUtil;
	@Autowired
	private WxUserMapper wxUserMapper;
	@Autowired
	private TeacherMapper teacherMapper;
	@Autowired
	private CollegeMapper collegeMapper;

	@Value("${excel.filePath}")
	private String filePath;
	@Value("${img.url}")
	private String imgUrl;

	@Transactional
	@Override
	public ResultVO insertGoods(InsertGoodsForm insertGoodsForm) {
		// 检查该老师有无超期物品
		List<Goods> goodsList = goodsMapper.selectByChargeNameAndTakeOutStatus(insertGoodsForm.getChargeName());
		for (Goods goods : goodsList) {
			if (goods.getOverdueStatus() == 1) {
				return ResultVOUtil.error(ResultEnum.OVERDUE_ERROR);
			}
		}
		Goods goods = new Goods();
		BeanUtils.copyProperties(insertGoodsForm, goods);
		College college = collegeMapper.selectByPrimaryKey(insertGoodsForm.getCollege());
		goods.setCollege(college.getCollegeName());
		// 生成随机的编号
		String goodsId = generateIdUtil.getRandomId(goodsMapper);
		goods.setGoodsId(goodsId);
		// 生成申请时间
		Date date = new Date();
		goods.setApplicationTime(date);
		// 初始化状态
		goods.setOverdueStatus(0);
		goods.setVerifyStatus(0);
		// 将物品信息插入数据库中
		int insert = goodsMapper.insert(goods);
		if (insert != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		// 物品详情
		GoodsInfo goodsInfo = new GoodsInfo();
		goodsInfo.setGoodsId(goodsId);
		for (GoodsInfoForm goodsInfoForm : insertGoodsForm.getGoodsInfoFormList()) {
			if (goodsInfoForm.getGoodsName() == null || goodsInfoForm.getGoodsNum() == null || goodsInfoForm.getGoodsWeight() == null) {
				continue;
			}
			BeanUtils.copyProperties(goodsInfoForm, goodsInfo);
			// 生成随机的编号
			String goodsInfoId = generateIdUtil.getRandomId(goodsInfoMapper);
			goodsInfo.setGoodsInfoId(goodsInfoId);
			int insert1 = goodsInfoMapper.insert(goodsInfo);
			if (insert1 != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		return ResultVOUtil.success("申请成功");
	}

	@Transactional
	@Override
	public ResultVO teacherVerify(TeacherVerifyForm teacherVerifyForm) {
		// 检查是否为老师
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(teacherVerifyForm.getOpenId());
		if (wxUser.getType() != 1) {
			return ResultVOUtil.error(ResultEnum.STUDENT_ROLE_ERROR);
		}

		Goods goods = goodsMapper.selectByPrimaryKey(teacherVerifyForm.getGoodsId());
		// 检查老师是否是该负责人
		if (!wxUser.getName().equals(goods.getChargeName())) {
			return ResultVOUtil.error(ResultEnum.TEACHER_VERIFY_ERROR);
		}
		if (goods == null) {
			return ResultVOUtil.error(ResultEnum.GOODS_NOT_EXIST_ERROR);
		}
		// 将审核状态置为1
		goods.setVerifyStatus(1);
		// 更新数据库
		goodsMapper.updateByPrimaryKey(goods);
		return ResultVOUtil.success("审核成功");

	}

	@Transactional
	@Override
	public ResultVO adminVerify(AdminVerifyForm adminVerifyForm) {
		Goods goods = goodsMapper.selectByPrimaryKey(adminVerifyForm.getGoodsId());
		if (goods == null) {
			return ResultVOUtil.error(ResultEnum.GOODS_NOT_EXIST_ERROR);
		}
		// 设置审核状态
		if (goods.getVerifyStatus() != 1) {
			return ResultVOUtil.error(ResultEnum.TEACHER_NOT_VERIFY_ERROR);
		}
		goods.setVerifyStatus(2);
		// 设置货架号
		goods.setShelfNumber(adminVerifyForm.getShelfNumber());
		// 设置取出状态
		goods.setTakeOutStatus(0);
		// 更新数据库
		int update = goodsMapper.updateByPrimaryKey(goods);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("审核成功");
	}

	@Override
	public ResultVO browseGoods(String teacherName) {
		List<GoodsVO> goodsVOList = new ArrayList<>();
		List<Goods> goodsList = goodsMapper.selectByChargeNameAndTakeOutStatus(teacherName);
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// 设置时间
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			goodsVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			goodsVO.setGoodsInfoList(goodsInfos);
			goodsVOList.add(goodsVO);
		}
		return ResultVOUtil.success(goodsVOList);
	}

	@Override
	public ResultVO adminBrowseAllWarehousingApplications() {
		List<WarehousingApplicationVO> warehousingApplicationVOList = new ArrayList<>();
		List<Goods> goodsList = goodsMapper.selectByVerifyStatus(1);
		for (Goods goods : goodsList) {
			WarehousingApplicationVO warehousingApplicationVO = new WarehousingApplicationVO();
			BeanUtils.copyProperties(goods, warehousingApplicationVO);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			warehousingApplicationVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			warehousingApplicationVO.setGoodsInfoList(goodsInfoList);
			warehousingApplicationVOList.add(warehousingApplicationVO);
		}
		return ResultVOUtil.success(warehousingApplicationVOList);
	}

	@Override
	public ResultVO teacherBrowseWarehousingApplications(String teacherName) {
		List<WarehousingApplicationVO> warehousingApplicationVOList = new ArrayList<>();
		List<Goods> goodsList = goodsMapper.selectByChargeNameAndVerifyStatus(teacherName, 0);
		for (Goods goods : goodsList) {
			WarehousingApplicationVO warehousingApplicationVO = new WarehousingApplicationVO();
			BeanUtils.copyProperties(goods, warehousingApplicationVO);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			warehousingApplicationVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			warehousingApplicationVO.setGoodsInfoList(goodsInfoList);
			warehousingApplicationVOList.add(warehousingApplicationVO);
		}
		return ResultVOUtil.success(warehousingApplicationVOList);
	}

	@Override
	public void exportExcel(HttpServletResponse response) {
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String format = simpleDateFormat.format(date);
		String filename = filePath + format + ".xlsx";
		// 2 调用easyexcel里面的方法实现写操作
		// write方法两个参数：第一个参数文件路径名称，第二个参数实体类class
		List<ExcelVO> excelVOList = new ArrayList<>();
		List<Goods> goodsList = goodsMapper.selectByVerifyStatus(2);
		for (Goods goods : goodsList) {
			ExcelVO excelVO = new ExcelVO();
			BeanUtils.copyProperties(goods, excelVO);
			// 设置时间
			excelVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			for (GoodsInfo goodsInfo : goodsInfoList) {
				if (goodsInfo.getGoodsNum() != 0) {
					ExcelVO excelVO1 = new ExcelVO();
					BeanUtils.copyProperties(goodsInfo, excelVO);
					BeanUtils.copyProperties(excelVO, excelVO1);
					excelVOList.add(excelVO1);
				}
			}
		}
		EasyExcel.write(filename, ExcelVO.class).sheet("库存列表").doWrite(excelVOList);
		FileUtil.downloadFile(response, filename);
		FileUtil.deleteFile(filename);
	}

	@Transactional
	@Override
	public ResultVO importExcel(MultipartFile file) {
		String fileName = FileUtil.generateFileName(file);
		FileUtil.upload(file, filePath, fileName);
		// 这里默认读取第一个sheet
		EasyExcel.read(filePath + fileName, ExcelForm.class, new GoodsListener(goodsMapper, goodsInfoMapper)).sheet().doRead();
		FileUtil.deleteFile(filePath+fileName);
		return ResultVOUtil.success("成功");

	}

	@Override
	@Scheduled(cron = "0 0 0 * * ?")
	public ResultVO checkOverdue() {
		List<Goods> goodsList = goodsMapper.selectAll();
		for (Goods goods : goodsList) {
			Date applicationTime = goods.getApplicationTime();
			Date date = new Date();
			LocalDate oldDate = applicationTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate now = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Period period = Period.between(oldDate, now);
			if (period.getYears() >= 2 && period.getDays() >= 0) {
				log.info("差{}年,{}月,{}天,老师：{}", period.getYears(), period.getMonths(),period.getDays(),goods.getChargeName());
				goods.setOverdueStatus(1);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}
		return ResultVOUtil.success("检查完成");
	}

	@Override
	public ResultVO selectWarehousingApplications(String teacherName) {
		List<Goods> goodsList = goodsMapper.selectReviewed(teacherName);
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// 设置时间
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			goodsVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			goodsVO.setGoodsInfoList(goodsInfos);
			goodsVOList.add(goodsVO);
		}
		return ResultVOUtil.success(goodsVOList);
	}

	@Override
	public ResultVO delete(DeleteForm deleteForm) {
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(deleteForm.getOpenId());
		Goods goods = goodsMapper.selectByPrimaryKey(deleteForm.getGoodsId());
		if(wxUser.getType() == 0 && !wxUser.getName().equals(goods.getAgentName())) {
			return ResultVOUtil.error(ResultEnum.NOT_ONESELF_ERROR);
		}
		if(wxUser.getType() == 0 && goods.getVerifyStatus() != 0){
			return ResultVOUtil.error(ResultEnum.STUDENT_ROLE_ERROR);
		}
		if(wxUser.getType() == 1 && !wxUser.getName().equals(goods.getChargeName())){
			return ResultVOUtil.error(ResultEnum.NOT_ONESELF_ERROR);
		}
		if(wxUser.getType() == 1 && goods.getVerifyStatus() == 2){
			return ResultVOUtil.error(ResultEnum.TEACHER_ROLE_ERROR);
		}
		// 删除
		goodsInfoMapper.deleteByGoodsId(goods.getGoodsId());
		goodsMapper.deleteByPrimaryKey(deleteForm.getGoodsId());
		return ResultVOUtil.success("删除成功");
	}

	@Override
	public ResultVO selectOverdueGoods() {
		List<Goods> goodsList = goodsMapper.selectByOverdueStatus(1);
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// 设置时间
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			goodsVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			goodsVO.setGoodsInfoList(goodsInfos);
			goodsVOList.add(goodsVO);
		}
		return ResultVOUtil.success(goodsVOList);
	}

	@Override
	public ResultVO select(String teacherName) {
		List<Goods> goodsList = goodsMapper.selectByChargeName(teacherName);
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// 设置时间
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			goodsVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			goodsVO.setGoodsInfoList(goodsInfos);
			goodsVOList.add(goodsVO);
		}
		return ResultVOUtil.success(goodsVOList);
	}

	@Override
	public ResultVO selectByStudentName(String openId) {
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(openId);
		if(wxUser == null){
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST_ERROR);
		}
		List<Goods> goodsList = goodsMapper.selectByStudentName(wxUser.getName());
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// 设置时间
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			goodsVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfos = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			goodsVO.setGoodsInfoList(goodsInfos);
			goodsVOList.add(goodsVO);
		}
		return ResultVOUtil.success(goodsVOList);
	}

	@Transactional
	@Override
	public ResultVO extension(ExtensionForm extensionForm) {
		Goods goods = goodsMapper.selectByPrimaryKey(extensionForm.getGoodsId());
		Date applicationTime = goods.getApplicationTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(applicationTime);
		calendar.add(Calendar.YEAR,extensionForm.getYear());
		calendar.add(Calendar.MONTH,extensionForm.getMonth());
		calendar.add(Calendar.DATE,extensionForm.getDay());
		goods.setApplicationTime(calendar.getTime());
		goods.setOverdueStatus(0);
		int update = goodsMapper.updateByPrimaryKey(goods);
		if(update != 1){
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("延期成功");
	}

	@Override
	public ResultVO selectCollege() {
		List<College> colleges = collegeMapper.selectAll();
		List<CollegeVO> collegeVOList =  new ArrayList<>();
		for (College college : colleges){
			CollegeVO collegeVO = new CollegeVO();
			collegeVO.setText(college.getCollegeName());
			collegeVO.setValue(college.getCollegeId());
			collegeVOList.add(collegeVO);
		}
		return ResultVOUtil.success(collegeVOList);
	}

	@Override
	public void download(HttpServletResponse response, String goodsId) {
		// 入库物品
		Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
		// 检查是否审核
		if (goods.getVerifyStatus() != 2) {
			response.setHeader("state","1");
			return;
		}
		// 入库物品详情
		List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goodsId);
		WordGo wordGo = new WordGo();
		wordGo.addLine("入库申请表", "font-size:一号;font-width:bold;text-align:center");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

		WordTable wordTable1 = new WordTable(4, 4,"row-height:1=5%,2=5%,3=5%,4=5%");
		wordTable1.add(1, 1, "学院（中心）", goods.getCollege());
		wordTable1.add(1, 3, "申请时间", sd.format(goods.getApplicationTime()));
		wordTable1.add(2, 1, "物品负责人手机", goods.getChargePhone());
		wordTable1.add(2, 3, "物品负责人姓名", goods.getChargeName());
		wordTable1.add(3, 1, "经办人手机", goods.getAgentPhone());
		wordTable1.add(3, 3, "经办人姓名", goods.getAgentName());
		wordTable1.add(4, 1, "存放地址", goods.getShelfNumber());
		wordGo.addTable(wordTable1);
		WordTable wordTable = new WordTable(goodsInfoList.size() + 1, 3,"row-height:1=5%,2=5%,3=5%,4=5%");
		wordTable.add(1, 1, "物品名称", "规格型号（L/kg）", "数量(桶/瓶）");
		for (int i = 0; i < goodsInfoList.size(); i++) {
			GoodsInfo goodsInfo = goodsInfoList.get(i);
			wordTable.add(i + 2, 1, goodsInfo.getGoodsName(), goodsInfo.getGoodsWeight().toString(), goodsInfo.getGoodsNum().toString());
		}
		wordGo.addTable(wordTable);
		wordGo.addImg(imgUrl, " new-line:true; position: absolute; left: 10px; top:140px; width:300px; height:250px");
		wordGo.create(filePath + goodsId + ".docx");

		FileUtil.downloadFile(response, filePath + goodsId + ".docx");
		FileUtil.deleteFile(filePath + goodsId + ".docx");
	}
}
