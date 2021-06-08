package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.WxUser;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface WxUserMapper {
    int deleteByPrimaryKey(String openId);

    int insert(WxUser record);

    WxUser selectByPrimaryKey(String openId);

    WxUser selectByStuNum(String stuNum);

    List<WxUser> selectAll();

    int updateByPrimaryKey(WxUser record);
}