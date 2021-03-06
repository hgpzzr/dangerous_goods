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
import java.util.*;

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
	private String excelFilePath;
	@Value("${word.filePath}")
	private String wordFilePath;
	@Value("${word.inTemplatePath}")
	private String inTemplatePath;
	@Value("${img.url}")
	private String imgUrl;

	@Transactional
	@Override
	public ResultVO insertGoods(InsertGoodsForm insertGoodsForm) {
		// ??????????????????
		for (GoodsInfoForm goodsInfoForm : insertGoodsForm.getGoodsInfoFormList()) {
			if (goodsInfoForm.getGoodsWeight() < 0 || goodsInfoForm.getGoodsNum() < 1) {
				return ResultVOUtil.error(ResultEnum.DIGITAL_SPECIFICATION_ERROR);
			}
		}
		// ??????????????????????????????
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(insertGoodsForm.getOpenId());
		if (!wxUser.getTeacherName().equals(insertGoodsForm.getChargeName())) {
			return ResultVOUtil.error(ResultEnum.NOT_SELF_TEACHER_ERROR);
		}
		// ?????????????????????????????????
		List<Goods> goodsList = goodsMapper.selectByChargeNameAndTakeOutStatus(insertGoodsForm.getChargeName());
		for (Goods goods : goodsList) {
			if (goods.getAccessControl() == 1) {
				return ResultVOUtil.error(ResultEnum.OVERDUE_ERROR);
			}
		}
		Goods goods = new Goods();
		BeanUtils.copyProperties(insertGoodsForm, goods);
		College college = collegeMapper.selectByPrimaryKey(insertGoodsForm.getCollege());
		goods.setCollege(college.getCollegeName());
		// ?????????????????????
		String goodsId = generateIdUtil.getRandomId(goodsMapper);
		goods.setGoodsId(goodsId);
		// ??????????????????
		Date date = new Date();
		goods.setApplicationTime(date);
		// ???????????????
		goods.setOverdueStatus(0);
		goods.setVerifyStatus(0);
		goods.setTakeOutStatus(0);
		// ?????????????????????????????????
		int insert = goodsMapper.insert(goods);
		if (insert != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		// ????????????
		GoodsInfo goodsInfo = new GoodsInfo();
		goodsInfo.setGoodsId(goodsId);
		for (GoodsInfoForm goodsInfoForm : insertGoodsForm.getGoodsInfoFormList()) {
			if (goodsInfoForm.getGoodsName() == null || goodsInfoForm.getGoodsNum() == null || goodsInfoForm.getGoodsWeight() == null) {
				continue;
			}
			BeanUtils.copyProperties(goodsInfoForm, goodsInfo);
			// ?????????????????????
			String goodsInfoId = generateIdUtil.getRandomId(goodsInfoMapper);
			goodsInfo.setGoodsInfoId(goodsInfoId);
			int insert1 = goodsInfoMapper.insert(goodsInfo);
			if (insert1 != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		return ResultVOUtil.success("????????????");
	}

	@Transactional
	@Override
	public ResultVO teacherVerify(TeacherVerifyForm teacherVerifyForm) {
		// ?????????????????????
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(teacherVerifyForm.getOpenId());
		if (wxUser.getType() != 1) {
			return ResultVOUtil.error(ResultEnum.STUDENT_ROLE_ERROR);
		}

		Goods goods = goodsMapper.selectByPrimaryKey(teacherVerifyForm.getGoodsId());
		// ?????????????????????????????????
		if (!wxUser.getName().equals(goods.getChargeName())) {
			return ResultVOUtil.error(ResultEnum.TEACHER_VERIFY_ERROR);
		}
		if (goods == null) {
			return ResultVOUtil.error(ResultEnum.GOODS_NOT_EXIST_ERROR);
		}
		// ?????????????????????1
		goods.setVerifyStatus(1);
		// ???????????????
		goodsMapper.updateByPrimaryKey(goods);
		return ResultVOUtil.success("????????????");

	}

	@Transactional
	@Override
	public ResultVO adminVerify(AdminVerifyForm adminVerifyForm) {
		Goods goods = goodsMapper.selectByPrimaryKey(adminVerifyForm.getGoodsId());
		if (goods == null) {
			return ResultVOUtil.error(ResultEnum.GOODS_NOT_EXIST_ERROR);
		}
		// ??????????????????
		if (goods.getVerifyStatus() != 1) {
			return ResultVOUtil.error(ResultEnum.TEACHER_NOT_VERIFY_ERROR);
		}
		goods.setVerifyStatus(2);
		// ???????????????
		goods.setShelfNumber(adminVerifyForm.getShelfNumber());
		// ???????????????
		goods.setRoomNumber(adminVerifyForm.getRoomNumber());
		// ??????????????????
		goods.setTakeOutStatus(0);
		// ???????????????
		int update = goodsMapper.updateByPrimaryKey(goods);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO browseGoods(String teacherName) {
		List<GoodsVO> goodsVOList = new ArrayList<>();
		List<Goods> goodsList = goodsMapper.selectByChargeNameAndTakeOutStatus(teacherName);
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// ????????????
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
		String filename = excelFilePath + format + ".xlsx";
		// 2 ??????easyexcel??????????????????????????????
		// write?????????????????????????????????????????????????????????????????????????????????class
		List<ExcelVO> excelVOList = new ArrayList<>();
		List<Goods> goodsList = goodsMapper.selectByVerifyStatus(2);
		for (Goods goods : goodsList) {
			ExcelVO excelVO = new ExcelVO();
			BeanUtils.copyProperties(goods, excelVO);
			// ????????????
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
		EasyExcel.write(filename, ExcelVO.class).sheet("????????????").doWrite(excelVOList);
		FileUtil.downloadFile(response, filename);
		FileUtil.deleteFile(filename);
	}

	@Transactional
	@Override
	public ResultVO importExcel(MultipartFile file) {
		String fileName = FileUtil.generateFileName(file);
		FileUtil.upload(file, excelFilePath, fileName);
		// ???????????????????????????sheet
		EasyExcel.read(excelFilePath + fileName, ExcelForm.class, new GoodsListener(goodsMapper, goodsInfoMapper)).sheet().doRead();
		FileUtil.deleteFile(excelFilePath + fileName);
		return ResultVOUtil.success("??????");

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
				log.info("???{}???,{}???,{}???,?????????{}", period.getYears(), period.getMonths(), period.getDays(), goods.getChargeName());
				goods.setOverdueStatus(1);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO selectWarehousingApplications(String teacherName) {
		List<Goods> goodsList = goodsMapper.selectReviewed(teacherName);
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// ????????????
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
		if (wxUser.getType() == 0 && !wxUser.getName().equals(goods.getAgentName())) {
			return ResultVOUtil.error(ResultEnum.NOT_ONESELF_ERROR);
		}
		if (wxUser.getType() == 0 && goods.getVerifyStatus() != 0) {
			return ResultVOUtil.error(ResultEnum.STUDENT_ROLE_ERROR);
		}
		if (wxUser.getType() == 1 && !wxUser.getName().equals(goods.getChargeName())) {
			return ResultVOUtil.error(ResultEnum.NOT_ONESELF_ERROR);
		}
		if (wxUser.getType() == 1 && goods.getVerifyStatus() == 2) {
			return ResultVOUtil.error(ResultEnum.TEACHER_ROLE_ERROR);
		}
		// ??????
		goodsInfoMapper.deleteByGoodsId(goods.getGoodsId());
		goodsMapper.deleteByPrimaryKey(deleteForm.getGoodsId());
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO selectOverdueGoods() {
		List<Goods> goodsList = goodsMapper.selectByOverdueStatus(1);
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// ????????????
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
			log.info("goods:{}", goods.toString());
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// ????????????
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
		if (wxUser == null) {
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST_ERROR);
		}
		List<Goods> goodsList = goodsMapper.selectByStudentName(wxUser.getName());
		List<GoodsVO> goodsVOList = new ArrayList<>();
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			// ????????????
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
//		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(applicationTime);
//		calendar.setTime(date);
//		calendar.add(Calendar.YEAR,-1);
//		calendar.add(Calendar.MONTH,-6);
		calendar.add(Calendar.YEAR, extensionForm.getYear());
		calendar.add(Calendar.MONTH, extensionForm.getMonth());
		calendar.add(Calendar.DATE, extensionForm.getDay());
		goods.setApplicationTime(calendar.getTime());
		goods.setOverdueStatus(0);
		int update = goodsMapper.updateByPrimaryKey(goods);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO selectCollege() {
		List<College> colleges = collegeMapper.selectAll();
		List<CollegeVO> collegeVOList = new ArrayList<>();
		for (College college : colleges) {
			CollegeVO collegeVO = new CollegeVO();
			collegeVO.setText(college.getCollegeName());
			collegeVO.setValue(college.getCollegeId());
			collegeVOList.add(collegeVO);
		}
		return ResultVOUtil.success(collegeVOList);
	}

	@Override
	public void download(HttpServletResponse response, String goodsId) {
		// ????????????
		Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
		// ??????????????????
		if (goods.getVerifyStatus() != 2) {
			response.setHeader("state", "1");
			return;
		}
		// ??????????????????
		List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goodsId);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

// ??????????????????
		String templatePath = inTemplatePath;
		// ????????????
		String outPath = wordFilePath + goods.getGoodsId() + ".docx";

		Map<String, Object> paramMap = new HashMap<>(16);
		// ???????????????????????? ?????????????????? {str,str}
		paramMap.put("college", goods.getCollege());
		paramMap.put("applicationTime", sd.format(goods.getApplicationTime()));
		paramMap.put("chargePhone", goods.getChargePhone());
		paramMap.put("chargeName", goods.getChargePhone());
		paramMap.put("agentPhone", goods.getAgentPhone());
		paramMap.put("agentName", goods.getAgentName());
		paramMap.put("shelfNumber", goods.getShelfNumber());
		paramMap.put("roomNumber", goods.getRoomNumber());
		log.info("goods.getRoomNumber():{}", goods.getRoomNumber());
		int size = goodsInfoList.size();
		for (int i = 0; i < size; i++) {
			GoodsInfo goodsInfo = goodsInfoList.get(i);
			paramMap.put("goodsName" + i, goodsInfo.getGoodsName());
			paramMap.put("weight" + i, goodsInfo.getGoodsWeight());
			paramMap.put("number" + i, goodsInfo.getGoodsNum());
		}
		for (int i = size; i < 4; i++) {
			paramMap.put("goodsName" + i, "");
			paramMap.put("weight" + i, "");
			paramMap.put("number" + i, "");
		}
		com.example.dangerous_goods.utils.DynWordUtils.process(paramMap, templatePath, outPath);
		log.info("filePath:{}", wordFilePath + goodsId + ".docx");
		FileUtil.downloadFile(response, wordFilePath + goodsId + ".docx");
//		FileUtil.deleteFile(wordFilePath + goodsId + ".docx");
	}

	@Override
	public ResultVO adminRefuse(String goodsId) {
		Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
		goods.setVerifyStatus(3);
		int update = goodsMapper.updateByPrimaryKey(goods);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO selectAllGoods() {
		List<Goods> goodsList = goodsMapper.selectByVerifyStatus(2);
		List<GoodsVO> goodsVOList = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for (Goods goods : goodsList) {
			GoodsVO goodsVO = new GoodsVO();
			BeanUtils.copyProperties(goods, goodsVO);
			goodsVO.setApplicationTime(simpleDateFormat.format(goods.getApplicationTime()));
			List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goods.getGoodsId());
			goodsVO.setGoodsInfoList(goodsInfoList);
			goodsVOList.add(goodsVO);
		}
		return ResultVOUtil.success(goodsVOList);
	}

	@Override
	public ResultVO updateAccessControl(UpdateAccessControlForm form) {
		List<Goods> goodsList = goodsMapper.selectByChargeName(form.getChargeName());
		for (Goods goods : goodsList) {
			goods.setAccessControl(form.getAccessControl());
			int update = goodsMapper.updateByPrimaryKey(goods);
			if (update != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO checkAccessControl(String chargeName) {
		List<Goods> goodsList = goodsMapper.selectByChargeName(chargeName);
		for (Goods goods : goodsList) {
			if (goods.getAccessControl() == 1) {
				return ResultVOUtil.success(false);
			}
		}
		return ResultVOUtil.success(true);
	}

	@Override
	public ResultVO updateGoods(UpdateGodsForm form) {
		Goods goods = goodsMapper.selectByPrimaryKey(form.getGoodsId());
		BeanUtils.copyProperties(form, goods);
		int update = goodsMapper.updateByPrimaryKey(goods);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}

	@Override
	public ResultVO updateCharge(UpdateChargeForm form) {
		// ?????????????????????
		List<Teacher> teacherList = teacherMapper.selectByName(form.getChargeName());
		if (teacherList.size() == 0) {
			return ResultVOUtil.error(ResultEnum.TEACHER_NOT_EXIST_ERROR);
		}
		Goods goods = goodsMapper.selectByPrimaryKey(form.getGoodsId());
		BeanUtils.copyProperties(form, goods);
		int update = goodsMapper.updateByPrimaryKey(goods);
		if (update != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}

	@Transactional
	@Override
	public ResultVO adminAddGoods(adminAddGoodsForm form) {
		// ??????????????????
		for (GoodsInfoForm goodsInfoForm : form.getGoodsInfoFormList()) {
			if (goodsInfoForm.getGoodsWeight() < 0 || goodsInfoForm.getGoodsNum() < 1) {
				return ResultVOUtil.error(ResultEnum.DIGITAL_SPECIFICATION_ERROR);
			}
		}
		Goods goods = new Goods();
		String goodsId = GenerateIdUtil.getGoodsId(goodsMapper);
		BeanUtils.copyProperties(form, goods);
		goods.setOverdueStatus(0);
		goods.setVerifyStatus(2);
		goods.setTakeOutStatus(0);
		goods.setAccessControl(0);
		goods.setGoodsId(goodsId);
		int insert = goodsMapper.insert(goods);
		if (insert != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		// ????????????
		GoodsInfo goodsInfo = new GoodsInfo();
		goodsInfo.setGoodsId(goodsId);
		for (GoodsInfoForm goodsInfoForm : form.getGoodsInfoFormList()) {
			if (goodsInfoForm.getGoodsName() == null || goodsInfoForm.getGoodsNum() == null || goodsInfoForm.getGoodsWeight() == null) {
				continue;
			}
			BeanUtils.copyProperties(goodsInfoForm, goodsInfo);
			// ?????????????????????
			String goodsInfoId = generateIdUtil.getRandomId(goodsInfoMapper);
			goodsInfo.setGoodsInfoId(goodsInfoId);
			int insert1 = goodsInfoMapper.insert(goodsInfo);
			if (insert1 != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		return ResultVOUtil.success("????????????");
	}

	@Transactional
	@Override
	public ResultVO adminDeleteGoods(String goodsInfoId) {
		if (goodsInfoId == null) {
			return ResultVOUtil.error(ResultEnum.PARAM_NULL_ERROR);
		}
		GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(goodsInfoId);
		int delete = goodsInfoMapper.deleteByPrimaryKey(goodsInfoId);
		if (delete != 1) {
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		List<GoodsInfo> goodsInfoList = goodsInfoMapper.selectByGoodsId(goodsInfo.getGoodsId());
		if (goodsInfoList.size() == 0) {
			int delete1 = goodsMapper.deleteByPrimaryKey(goodsInfo.getGoodsId());
			if (delete1 != 1) {
				return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
			}
		}
		return ResultVOUtil.success("????????????");
	}

	@Transactional
	@Override
	public ResultVO changeGoodsNum(String goodsInfoId, Integer goodsNum) {
		if (goodsInfoId == null) {
			return ResultVOUtil.error(ResultEnum.PARAM_NULL_ERROR);
		}
		GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(goodsInfoId);
		// ??????????????????0
		if (goodsNum == 0) {
			goodsInfo.setTakeOutStatus(1);
			int flag = 0;
			// ????????????????????????0???????????????0????????????goods??????takeOutStatus??????0
			for (GoodsInfo goodsInfo1 : goodsInfoMapper.selectByGoodsId(goodsInfo.getGoodsId())) {
				if(goodsInfo1.getGoodsNum() != 0){
					flag = 1;
					break;
				}
			}
			if(flag == 0){
				Goods goods = goodsMapper.selectByPrimaryKey(goodsInfo.getGoodsId());
				goods.setTakeOutStatus(1);
				int update = goodsMapper.updateByPrimaryKey(goods);
				if(update != 1){
					return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
				}
			}
		}
		goodsInfo.setGoodsNum(goodsNum);
		int update = goodsInfoMapper.updateByPrimaryKey(goodsInfo);
		if(update != 1){
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("????????????");
	}
}
