package com.example.dangerous_goods.service.impl;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/6/2 19:22
 */
class GoodsServiceImplTest {

	@Test
	void checkOverdue() {
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.println(date);
	}
}