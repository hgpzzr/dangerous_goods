package com.example.dangerous_goods.enums;

import lombok.Getter;

/**
 * @author hgp
 * @version 1.0
 * @date 2021/3/29 19:45
 */
@Getter
public enum  ResultEnum {
    USER_EXIST_ERROR(1,"用户已存在"),
    DATABASE_OPTION_ERROR(2,"数据库操作失败"),
    USER_NOT_EXIST_ERROR(3,"用户不存在"),
    PASSWORD_ERROR(4,"密码错误"),
    STUDENT_ROLE_ERROR(5,"学生权限不足"),
    PASSWORD_LENGTH_ERROR(6,"密码长度错误"),
    GOODS_NOT_EXIST_ERROR(7,"商品不存在"),
    TEACHER_NOT_VERIFY_ERROR(8,"老师未审核"),
    OVERDUE_ERROR(9,"有超期物品"),
    TEACHER_VERIFY_ERROR(10,"必须由老师自己审核"),
    TEACHER_OR_STUDENT_EXIST_ERROR(10,"老师或学生已存在"),
    TEACHER_NOT_EXIST_ERROR(11,"老师不存在"),
    ADMIN_NOT_VERIFY_ERROR(12,"管理员未审核"),
    NOT_ONESELF_ERROR(13,"只能本人删除"),
    TEACHER_ROLE_ERROR(14,"老师权限不足"),
    UNDER_STOCK_ERROR(15,"库存不足"),
    NOT_SELF_TEACHER_ERROR(16,"只能填写自己的指导老师"),
    DIGITAL_SPECIFICATION_ERROR(17,"数字不符合规范"),




    AUTHENTICATION_ERROR(401, "用户认证失败,请重新登录"),
    PERMISSION_DENNY(403, "权限不足"),
    NOT_FOUND(404, "url错误,请求路径未找到"),
    SERVER_ERROR(500, "服务器未知错误:%s"),
    BIND_ERROR(511, "参数校验错误:%s"),
    REQUEST_METHOD_ERROR(550, "不支持%s的请求方式");

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
