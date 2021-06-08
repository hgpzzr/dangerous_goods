package com.example.dangerous_goods.dao;

import com.example.dangerous_goods.entity.Teacher;
import com.example.dangerous_goods.form.AddTeacherForm;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TeacherMapper {
    int deleteByPrimaryKey(String jobId);

    int insert(Teacher record);

    int batchInsert(List<AddTeacherForm> list);

    Teacher selectByPrimaryKey(String jobId);

    List<Teacher> selectByName(String name);

    List<Teacher> selectAll();

    int updateByPrimaryKey(Teacher record);
}