package com.example.dangerous_goods.service.impl;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.dao.TeacherMapper;
import com.example.dangerous_goods.dao.WxUserMapper;
import com.example.dangerous_goods.entity.Teacher;
import com.example.dangerous_goods.entity.WxUser;
import com.example.dangerous_goods.enums.ResultEnum;
import com.example.dangerous_goods.form.WxRegisterForm;
import com.example.dangerous_goods.service.WxUserService;
import com.example.dangerous_goods.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.chrono.IsoEra;
import java.util.List;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/1 16:12
 */
@Service
public class WxUserServiceImpl implements WxUserService {
	@Autowired
	private WxUserMapper wxUserMapper;
	@Autowired
	private TeacherMapper teacherMapper;

	@Override
	public ResultVO register(WxRegisterForm wxRegisterForm) {
		WxUser wxUser1 = wxUserMapper.selectByStuNum(wxRegisterForm.getStuNum());
		if(wxUser1 != null){
			return ResultVOUtil.error(ResultEnum.TEACHER_OR_STUDENT_EXIST_ERROR);
		}
		// 判断老师是否存在
		if(wxRegisterForm.getType() == 0){
			List<Teacher> teachers = teacherMapper.selectByName(wxRegisterForm.getTeacherName());
			if(teachers.size() == 0){
				return ResultVOUtil.error(ResultEnum.TEACHER_NOT_EXIST_ERROR);
			}
		}
		else {
			Teacher teacher = teacherMapper.selectByPrimaryKey(wxRegisterForm.getStuNum());
			if(teacher == null){
				return ResultVOUtil.error(ResultEnum.TEACHER_NOT_EXIST_ERROR);
			}
		}
		WxUser wxUser = new WxUser();
		BeanUtils.copyProperties(wxRegisterForm,wxUser);
		if(wxRegisterForm.getType() == 1){
			wxUser.setTeacherName(wxRegisterForm.getName());
		}
		wxUser.setStatus(1);
		int insert = wxUserMapper.insert(wxUser);
		if(insert != 1){
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("录入成功");
	}

	@Override
	public boolean isExit(String openId) {
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(openId);
		if(wxUser != null){
			return true;
		}
		return false;
	}

	@Override
	public ResultVO browseUserInfo(String openId) {
		WxUser wxUser = wxUserMapper.selectByPrimaryKey(openId);
		return ResultVOUtil.success(wxUser);
	}
}
