package com.example.dangerous_goods.service;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.form.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 21:21
 */
@Service
public interface GoodsService {
	/**
	 * 申请入库
	 * @param insertGoodsForm
	 * @return
	 */
	ResultVO insertGoods(InsertGoodsForm insertGoodsForm);

	/**
	 * 老师审核
	 * @param teacherVerifyForm
	 * @return
	 */
	ResultVO teacherVerify(TeacherVerifyForm teacherVerifyForm);

	/**
	 * 管理员审核
	 * @param adminVerifyForm
	 * @return
	 */
	ResultVO adminVerify(AdminVerifyForm adminVerifyForm);

	/**
	 * 根据老师查询未出库的危险品
	 * @param teacherName
	 * @return
	 */
	ResultVO browseGoods(String teacherName);

	/**
	 * 管理员查询所有老师审核过的入库申请
	 * @return
	 */
	ResultVO adminBrowseAllWarehousingApplications();

	/**
	 * 老师根据老师名字浏览入库申请
	 * @param teacherName
	 * @return
	 */
	ResultVO teacherBrowseWarehousingApplications(String teacherName);

	/**
	 * 导出excel
	 * @param response
	 */
	void exportExcel(HttpServletResponse response);

	/**
	 * 根据excel导入
	 * @param file
	 * @return
	 */
	ResultVO importExcel(MultipartFile file);

	/**
	 * 检查超期
	 * @return
	 */
	ResultVO checkOverdue();

	/**
	 * 查找老师审核过的和管理员审核过的入库申请
	 * @param teacherName
	 * @return
	 */
	ResultVO selectWarehousingApplications(String teacherName);

	/**
	 * 删除入库申请
	 * @param deleteForm
	 * @return
	 */
	ResultVO delete(DeleteForm deleteForm);

	/**
	 * 查询超期物品
	 * @return
	 */
	ResultVO selectOverdueGoods();

	/**
	 * 根据老师名字查询所有入库申请
	 * @param teacherName
	 * @return
	 */
	ResultVO select(String teacherName);

	/**
	 * 根据学生名字查询
	 * @param openId
	 * @return
	 */
	ResultVO selectByStudentName(String openId);

	/**
	 * 延期
	 * @param extensionForm
	 * @return
	 */
	ResultVO extension(ExtensionForm extensionForm);

	/**
	 * 查看所有学院
	 * @return
	 */
	ResultVO selectCollege();

	/**
	 * 下载入库凭证
	 * @param response
	 * @param goodsId
	 */
	void download(HttpServletResponse response,String goodsId);
}
