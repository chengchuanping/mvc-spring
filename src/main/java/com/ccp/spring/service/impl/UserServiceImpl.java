package com.ccp.spring.service.impl;

import com.ccp.spring.annotation.Autowired;
import com.ccp.spring.annotation.Service;
import com.ccp.spring.mapper.UserMapper;
import com.ccp.spring.service.UserService;

/**
 * 
 * @Description:
 *
 * @Author 程传平
 *
 * @Time 2020-05-03 00:02
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public String query(String name, int age) {

		return "name=".concat(name);
	}

	
}
