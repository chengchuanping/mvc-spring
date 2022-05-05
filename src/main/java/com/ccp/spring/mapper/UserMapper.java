package com.ccp.spring.mapper;

import com.ccp.spring.annotation.Mapper;
import com.ccp.spring.entiey.User;
@Mapper
public interface UserMapper extends BaseMapper<User> {

	User selectOne();

}
