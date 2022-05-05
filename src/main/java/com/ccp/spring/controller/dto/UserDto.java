package com.ccp.spring.controller.dto;

import java.io.Serializable;

public class UserDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8182683438350138678L;
	
	private int id;
	
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "{\"id\":\"" + id + "\", \"name\":\"" + name + "\" }";
	}

}
