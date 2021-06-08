package com.example.dangerous_goods.entity;

import java.io.Serializable;
import java.util.Date;

public class OutOfStock implements Serializable {
    private String outId;

    private Date outTime;

    private String filePath;

    private Integer verifyStatus;

    private String goodsId;

    private String agentName;

    private String agentPhone;

    private String chargeName;

    private String deliveryAddress;

    private static final long serialVersionUID = 1L;

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }
    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId == null ? null : outId.trim();
    }

    public Date getOutTime() {
        return outTime;
    }

    public void setOutTime(Date outTime) {
        this.outTime = outTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId == null ? null : goodsId.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", outId=").append(outId);
        sb.append(", outTime=").append(outTime);
        sb.append(", filePath=").append(filePath);
        sb.append(", verifyStatus=").append(verifyStatus);
        sb.append(", goodsId=").append(goodsId);
        sb.append(", agentName=").append(agentName);
        sb.append(", agentPhone=").append(agentPhone);
        sb.append(", chargeName=").append(chargeName);
        sb.append(", deliveryAddress=").append(deliveryAddress);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}