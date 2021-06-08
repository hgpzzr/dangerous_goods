package com.example.dangerous_goods.entity;

import java.io.Serializable;

public class OutOfStockInfo implements Serializable {
    private String outInfoId;

    private String outId;

    private String goodsName;

    private Integer goodsNum;

    private Double goodsWeight;

    public Double getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(Double goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    private static final long serialVersionUID = 1L;

    public String getOutInfoId() {
        return outInfoId;
    }

    public void setOutInfoId(String outInfoId) {
        this.outInfoId = outInfoId == null ? null : outInfoId.trim();
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId == null ? null : outId.trim();
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", outInfoId=").append(outInfoId);
        sb.append(", outId=").append(outId);
        sb.append(", goodsName=").append(goodsName);
        sb.append(", goodsNum=").append(goodsNum);
        sb.append(", goodsWeight=").append(goodsWeight);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}