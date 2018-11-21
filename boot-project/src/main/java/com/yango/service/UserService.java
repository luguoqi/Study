package com.yango.service;

import com.github.pagehelper.PageInfo;
import com.yango.entity.User;

public interface UserService {

    public User getUserById(int userId);

    boolean addUser(User record);
    public PageInfo<User> findAllUser(int pageNum, int pageSize);
}
