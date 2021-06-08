package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(String userId);

    int insert(User record);

    User selectByPrimaryKey(String userId);

    User getUserByUserName(String userName);

    List<User> selectAll();

    int updateByPrimaryKey(User record);
}