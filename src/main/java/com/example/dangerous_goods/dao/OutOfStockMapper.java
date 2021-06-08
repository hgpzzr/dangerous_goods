package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.OutOfStock;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OutOfStockMapper {
    int deleteByPrimaryKey(String outId);

    int insert(OutOfStock record);

    OutOfStock selectByPrimaryKey(String outId);

    List<OutOfStock> selectAll();

    List<OutOfStock> selectByVerifyStatus(Integer status);

    List<OutOfStock> selectByTeacherNameAndVerifyStatus(Integer status,String chargeName);

    int updateByPrimaryKey(OutOfStock record);
}