package com.ccp.spring.mapper;

import java.io.Serializable;


public interface BaseMapper<T> extends Mapper<T> {

	/**
	 * 插入一条记录
	 *
	 * @param entity 实体对象
	 */
	int insert(T entity);

	/**
	 * 根据 ID 删除
	 *
	 * @param id 主键ID
	 */
	int deleteById(Serializable id);

	/**
	 * 根据实体(ID)删除
	 *
	 * @param entity 实体对象
	 * @since 3.4.4
	 */
	int deleteById(T entity);

}
