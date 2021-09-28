package com.example.dangerous_goods.entity;

import java.io.Serializable;
import java.util.Date;

public class Goods implements Serializable {
    private String goodsId;

    private String college;

    private Date applicationTime;

    private String chargePhone;

    private String chargeName;

    private String agentPhone;

    private String agentName;

    private Integer overdueStatus;

    private Integer verifyStatus;

    private String shelfNumber;

    private Integer takeOutStatus;

    private String roomNumber;

    private Integer accessControl;

    private static final long serialVersionUID = 1L;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId == null ? null : goodsId.trim();
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college == null ? null : college.trim();
    }

    public Date getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(Date applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getChargePhone() {
        return chargePhone;
    }

    public void setChargePhone(String chargePhone) {
        this.chargePhone = chargePhone == null ? null : chargePhone.trim();
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName == null ? null : chargeName.trim();
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone == null ? null : agentPhone.trim();
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName == null ? null : agentName.trim();
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber == null ? null : roomNumber.trim();
    }

    public Integer getOverdueStatus() {
        return overdueStatus;
    }

    public void setOverdueStatus(Integer overdueStatus) {
        this.overdueStatus = overdueStatus;
    }

    public Integer getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(Integer verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber == null ? null : shelfNumber.trim();
    }

    public Integer getTakeOutStatus() {
        return takeOutStatus;
    }

    public void setTakeOutStatus(Integer takeOutStatus) {
        this.takeOutStatus = takeOutStatus;
    }

    public Integer getAccessControl() {
        return accessControl;
    }

    public void setAccessControl(Integer accessControl) {
        this.accessControl = accessControl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", goodsId=").append(goodsId);
        sb.append(", college=").append(college);
        sb.append(", applicationTime=").append(applicationTime);
        sb.append(", chargePhone=").append(chargePhone);
        sb.append(", chargeName=").append(chargeName);
        sb.append(", agentPhone=").append(agentPhone);
        sb.append(", agentName=").append(agentName);
        sb.append(", overdueStatus=").append(overdueStatus);
        sb.append(", verifyStatus=").append(verifyStatus);
        sb.append(", shelfNumber=").append(shelfNumber);
        sb.append(", takeOutStatus=").append(takeOutStatus);
        sb.append(", roomNumber=").append(roomNumber);
        sb.append(", accessControl=").append(accessControl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}