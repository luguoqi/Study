package com.yango.dao;

import com.yango.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    int insertSelective(User record);
    User selectByPrimaryKey(Integer id);

    List<User> selectUsers();
}
