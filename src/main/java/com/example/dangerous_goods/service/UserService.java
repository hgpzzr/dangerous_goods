package com.example.dangerous_goods.service;

import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.entity.User;
import com.example.dangerous_goods.form.AddTeacherForm;
import com.example.dangerous_goods.form.LoginForm;
import com.example.dangerous_goods.form.RegisterForm;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 16:53
 */

@Service
public interface UserService {
	/**
	 * 获取当前用户
	 * @return
	 */
	User getCurrentUser();

	/**
	 * 根据用户名获得用户
	 * @param userName
	 * @return
	 */
	User getUserByUserName(String userName);

	/**
	 * 登录
	 * @param loginForm
	 * @param response
	 * @return
	 */
	ResultVO login(LoginForm loginForm, HttpServletResponse response);

	/**
	 * 注册
	 * @param registerForm
	 * @return
	 */
	ResultVO register(RegisterForm registerForm);


	/**
	 * 添加老师
	 * @param file
	 * @return
	 */
	ResultVO addTeacher(MultipartFile file);




}
