package com.example.dangerous_goods.utils;


import com.example.dangerous_goods.dao.GoodsInfoMapper;
import com.example.dangerous_goods.dao.GoodsMapper;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Random;

/**
 * @Author HGP
 * @create 2020/9/11 20:32
 */
@Log
@Slf4j
@Component
public class GenerateIdUtil<T> {


    @SneakyThrows
    public String getRandomId(T t) {
        String num;
        Class interfaceImpl = t.getClass();
        Method method = interfaceImpl.getMethod("selectByPrimaryKey", String.class);
        do {
            Random random = new Random();
            int randomNum = random.nextInt(89999999);
            int intNum = randomNum + 10000000;
            num = String.valueOf(intNum);
        } while (method.invoke(t, num) != null);
        return num;
    }

    public static String getGoodsId(GoodsMapper goodsMapper){
        String num;
        do {
            Random random = new Random();
            int randomNum = random.nextInt(89999999);
            int intNum = randomNum + 10000000;
            num = String.valueOf(intNum);
        } while (goodsMapper.selectByPrimaryKey(num) != null);
        return num;
    }

    public static String getGoodsInfoId(GoodsInfoMapper goodsInfoMapper){
        String num;
        do {
            Random random = new Random();
            int randomNum = random.nextInt(89999999);
            int intNum = randomNum + 10000000;
            num = String.valueOf(intNum);
        } while (goodsInfoMapper.selectByPrimaryKey(num) != null);
        return num;
    }


}
