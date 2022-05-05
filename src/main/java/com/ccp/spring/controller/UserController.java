package com.ccp.spring.controller;

import javax.servlet.http.HttpServletResponse;

import com.ccp.spring.annotation.Autowired;
import com.ccp.spring.annotation.Controller;
import com.ccp.spring.annotation.RequestBody;
import com.ccp.spring.annotation.RequestMapping;
import com.ccp.spring.annotation.RequestMethod;
import com.ccp.spring.annotation.RequestParam;
import com.ccp.spring.controller.dto.UserDto;
import com.ccp.spring.service.UserService;

/**
 * 
 * @Description:
 *
 * @Author 程传平
 *
 * @Time 2020-05-02 23:39
 *
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping("/query")
	public void getUser(HttpServletResponse response, @RequestParam("name") String name,
			@RequestParam("age") String age) {

		 userService.query(name, Integer.valueOf(age));

	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateUser(@RequestBody UserDto dto) {
		return dto.toString();
	}

}
