package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.College;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeMapper {
    int deleteByPrimaryKey(Integer collegeId);

    int insert(College record);

    College selectByPrimaryKey(Integer collegeId);

    List<College> selectAll();

    int updateByPrimaryKey(College record);
}