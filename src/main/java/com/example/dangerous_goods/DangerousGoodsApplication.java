package com.example.dangerous_goods;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.dangerous_goods.dao")
public class DangerousGoodsApplication {

	public static void main(String[] args) {
		SpringApplication.run(DangerousGoodsApplication.class, args);
	}

}
