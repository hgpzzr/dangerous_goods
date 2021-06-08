package com.example.dangerous_goods.service.impl;

import com.example.dangerous_goods.VO.GoodsVO;
import com.example.dangerous_goods.VO.ResultVO;
import com.example.dangerous_goods.dao.GoodsInfoMapper;
import com.example.dangerous_goods.dao.GoodsMapper;
import com.example.dangerous_goods.dao.UserMapper;
import com.example.dangerous_goods.entity.Goods;
import com.example.dangerous_goods.entity.GoodsInfo;
import com.example.dangerous_goods.entity.User;
import com.example.dangerous_goods.enums.ResultEnum;
import com.example.dangerous_goods.form.LoginForm;
import com.example.dangerous_goods.form.RegisterForm;
import com.example.dangerous_goods.security.JwtProperties;
import com.example.dangerous_goods.security.JwtUserDetailServiceImpl;
import com.example.dangerous_goods.service.UserService;
import com.example.dangerous_goods.utils.GenerateIdUtil;
import com.example.dangerous_goods.utils.JwtTokenUtil;
import com.example.dangerous_goods.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/5/31 17:07
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private JwtUserDetailServiceImpl jwtUserDetailService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private JwtProperties jwtProperties;
	@Autowired
	private GenerateIdUtil generateIdUtil;

	@Override
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userName = authentication.getName();
		String key = "anonymousUser";
		if (!userName.equals(key)) {
			return getUserByUserName(userName);
		}
		return null;
	}

	@Override
	public User getUserByUserName(String userName) {
		User user = userMapper.getUserByUserName(userName);
		return user;
	}

	@Override
	public ResultVO login(LoginForm loginForm, HttpServletResponse response) {
		User user = getUserByUserName(loginForm.getUserName());
		if (null == user) {
			return ResultVOUtil.error(ResultEnum.USER_NOT_EXIST_ERROR);
		}
		UserDetails userDetails = jwtUserDetailService.loadUserByUsername(loginForm.getUserName());
		if (!(new BCryptPasswordEncoder().matches(loginForm.getPassword(), userDetails.getPassword()))) {
			return ResultVOUtil.error(ResultEnum.PASSWORD_ERROR);
		}
		Authentication token = new UsernamePasswordAuthenticationToken(loginForm.getUserName(), loginForm.getPassword(), userDetails.getAuthorities());
		Authentication authentication = authenticationManager.authenticate(token);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		final String realToken = jwtTokenUtil.generateToken(userDetails);
		response.addHeader(jwtProperties.getTokenName(), realToken);
		Map<String, Serializable> map = new HashMap<>();
		map.put("name",user.getUserName());
		map.put("userId",user.getUserId());
		map.put("token",realToken);
		return ResultVOUtil.success(map);
	}

	@Override
	public ResultVO register(RegisterForm registerForm) {
		boolean isHave = userMapper.getUserByUserName(registerForm.getUserName()) != null;
		if(isHave){
			return ResultVOUtil.error(ResultEnum.USER_EXIST_ERROR);
		}
		// 判断密码长度
		if(registerForm.getPassword().length()<6 || registerForm.getPassword().length()>18){
			return ResultVOUtil.error(ResultEnum.PASSWORD_LENGTH_ERROR);
		}
		User user = new User();
		BeanUtils.copyProperties(registerForm,user);
		user.setRole(1);
		user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
		// 生成随机的userId
		String userId = generateIdUtil.getRandomId(userMapper);
		user.setUserId(userId);
		// 存入数据库
		int insert = userMapper.insert(user);
		if(insert != 1){
			return ResultVOUtil.error(ResultEnum.DATABASE_OPTION_ERROR);
		}
		return ResultVOUtil.success("注册成功");
	}


}
