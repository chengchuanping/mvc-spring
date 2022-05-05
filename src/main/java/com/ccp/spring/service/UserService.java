package com.ccp.spring.service;

import com.ccp.spring.entiey.User;

/**
 * 
 * @Description:
 *
 * @Author 程传平
 *
 * @Time 2020-05-02 23:40
 *
 */
public interface UserService extends BaseService<User> {

	public String query(String name, int age);

}
