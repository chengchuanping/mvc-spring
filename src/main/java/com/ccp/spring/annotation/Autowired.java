package com.ccp.spring.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 
 * @Description: 
 *
 * @Author 程传平
 *
 * @Time   2020-05-02 23:40
 *
 */
@Target({FIELD})
@Retention(RUNTIME)
public @interface Autowired {

	String value() default "";
}
