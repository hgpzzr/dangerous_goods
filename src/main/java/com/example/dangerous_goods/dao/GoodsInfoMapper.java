package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.GoodsInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsInfoMapper {
    int deleteByPrimaryKey(String goodsInfoId);

    int deleteByGoodsId(String goodsId);

    int insert(GoodsInfo record);

    int batchInsert(List<GoodsInfo> list);

    GoodsInfo selectByPrimaryKey(String goodsInfoId);

    GoodsInfo selectByGoodsIdAndGoodsName(String goodsId,String goodsName);

    List<GoodsInfo> selectAll();

    List<GoodsInfo> selectByGoodsId(String goodsId);

    int updateByPrimaryKey(GoodsInfo record);
}