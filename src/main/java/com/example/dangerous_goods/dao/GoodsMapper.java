package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.Goods;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GoodsMapper {
    int deleteByPrimaryKey(String goodsId);

    int insert(Goods record);

    Goods selectByPrimaryKey(String goodsId);

    List<Goods> selectByChargeNameAndTakeOutStatus(String chargeName);

    List<Goods> selectByChargeName(String chargeName);

    List<Goods> selectByChargeNameAndVerifyStatus(String chargeName, Integer verifyStatus);

    List<Goods> selectReviewed(String teacherName);

    List<Goods> selectByVerifyStatus(Integer verifyStatus);

    List<Goods> selectAll();

    List<Goods> selectByTakeOutStatus(Integer takeOutStatus);

    List<Goods> selectByOverdueStatus(Integer overdueStatus);

    List<Goods> selectByStudentName(String studentName);

    int batchInsert(List<Goods> goodsList);

    int updateByPrimaryKey(Goods record);
}