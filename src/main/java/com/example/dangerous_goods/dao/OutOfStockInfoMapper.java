package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.OutOfStockInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OutOfStockInfoMapper {
    int deleteByPrimaryKey(String outInfoId);

    int insert(OutOfStockInfo record);

    OutOfStockInfo selectByPrimaryKey(String outInfoId);

    List<OutOfStockInfo> selectAll();

    List<OutOfStockInfo> selectByOutId(String outId);

    int updateByPrimaryKey(OutOfStockInfo record);
}