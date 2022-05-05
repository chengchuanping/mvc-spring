package com.ccp.spring.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccp.spring.annotation.Autowired;
import com.ccp.spring.annotation.Controller;
import com.ccp.spring.annotation.RequestBody;
import com.ccp.spring.annotation.RequestMapping;
import com.ccp.spring.annotation.RequestParam;
import com.ccp.spring.annotation.Service;

/**
 * 
 * @Description:
 *
 * @Author 程传平
 *
 * @Time 2020-05-02 21:36
 *
 */
public class DispatcherServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<String> classNames = new ArrayList<String>();

	Map<String, Object> beans = new HashMap<String, Object>();

	Map<String, Object> handlerMap = new HashMap<String, Object>();

	public void init(ServletConfig config) {
		//
		scanPackage("com.ccp"); // 写到properties

		// 根据全类名创建Bean
		doInstance();

		// IOC 根据Bean依赖注入
		doIoc();

		buildUrlMapping(); // xxx/xxx---->methd 建立映射关系
	}

	private void buildUrlMapping() {

		if (beans.isEmpty()) {
			return;
		}

		for (Map.Entry<String, Object> entry : beans.entrySet()) {

			Object instance = entry.getValue();

			Class<?> clazz = instance.getClass();

			if (clazz.isAnnotationPresent(Controller.class)) {

				RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);

				String classPath = requestMapping.value();

				Method[] methods = clazz.getMethods();

				for (Method method : methods) {

					if (method.isAnnotationPresent(RequestMapping.class)) {

						RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);

						String methodPath = methodMapping.value();

						// xxx/xxx---->method
						handlerMap.put(classPath.concat(methodPath), method);

					} else {

						continue;

					}
				}
			} else {
				continue;
			}

		}
	}

	/**
	 * 把service注入到controller
	 */
	private void doIoc() {
		if (beans.isEmpty()) {
			return;
		}
		for (Map.Entry<String, Object> entry : beans.entrySet()) {

			Object instance = entry.getValue();

			Class<?> clazz = instance.getClass();

			if (clazz.isAnnotationPresent(Controller.class)) {

				Field[] fileds = clazz.getDeclaredFields();

				for (Field field : fileds) {

					if (field.isAnnotationPresent(Autowired.class)) {

						field.setAccessible(true);

						Autowired autowired = field.getAnnotation(Autowired.class);

						String key = autowired.value();

						try {

							field.set(instance, beans.get(key));

						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						continue;
					}

				}
			} else {
				continue;
			}
		}
	}

	/**
	 * 根据扫描的扫描的全类名、进行实例化
	 * <p>
	 * classNames
	 * </p>
	 */
	private void doInstance() {
		if (classNames.size() <= 0) {
			return;
		}
		// 遍历classNames的所有class类，对这些类进行
		for (String className : classNames) {
			try {
				Class<?> clazz = Class.forName(className.replace(".class", ""));// com.ccp.spring.UserController

				if (clazz.isAnnotationPresent(Controller.class)) {

					Object instance = clazz.newInstance();// 创建控制类

					RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);

					String rmvalue = requestMapping.value();// @RequestMapping("/user")括号中的/user

					beans.put(rmvalue, instance);

				} else if (clazz.isAnnotationPresent(Service.class)) {

					Service service = clazz.getAnnotation(Service.class);

					Object instance = clazz.newInstance();// 创建控制类

					beans.put(service.value(), instance);

				} else {
					continue;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 把所有的类扫描出来--所有的class文件
	 * 
	 * @param basePackage
	 */
	private void scanPackage(String basePackage) {
		URL url = this.getClass().getClassLoader().getResource("/".concat(basePackage.replaceAll("\\.", "/")));

		String fileStr = url.getFile();

		File file = new File(fileStr);

		String[] filesStr = file.list();

		for (String path : filesStr) {

			File filePath = new File(fileStr.concat(path)); // com.ccp.spring

			if (filePath.isDirectory()) {
				scanPackage(basePackage.concat(".".concat(path)));
			} else {
				// 加入到List
				classNames.add(basePackage.concat(".").concat(filePath.getName())); // com.ccp.spring...xxx.class
			}
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {

		// http://172.0.0.1:8080/mvc-spring/user/getUser?name=&age=

		// 获取请求路径
		String url = req.getRequestURI();

		String context = req.getContextPath();// 得到 mvc-spring

		String path = url.replace(context, "");// 得到user/getUser

		Method method = (Method) handlerMap.get(path);

		// 根据key=/user到map中去取
		Object object = beans.get("/".concat(path.split("/")[1]));

		Object[] arg = hand(req, response, method);

		try {

			Object result = method.invoke(object, arg);
			if (Objects.nonNull(result)) {
//				response.setCharacterEncoding("UTF-8");
				response.setHeader("content-type", "text/html;charset=UTF-8");

				PrintWriter pw = response.getWriter();

				pw.write(result.toString());
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method) {

		String contentType = request.getContentType();

		// System.err.println(contentType);

		if ("application/json".equals(contentType)) {

		}

		// 拿到当前待执行方法有哪些参数

		Class<?>[] paramCclazzs = method.getParameterTypes();

		Object[] args = new Object[paramCclazzs.length];

		int args_i = 0;

		int index = 0;

		for (Class<?> paramCclazz : paramCclazzs) {

			if (ServletRequest.class.isAssignableFrom(paramCclazz)) {
				args[args_i++] = request;
			}
			if (ServletResponse.class.isAssignableFrom(paramCclazz)) {
				args[args_i++] = response;
			}

			Annotation[] paramAns = method.getParameterAnnotations()[index];

			for (Annotation paramAn : paramAns) {

				if (RequestParam.class.isAssignableFrom(paramAn.getClass())) {

					RequestParam rp = (RequestParam) paramAn;

					args[args_i++] = request.getParameter(rp.value());
				}

				if (RequestBody.class.isAssignableFrom(paramAn.getClass())) {

					try {

						BufferedReader streamReader = new BufferedReader(
								new InputStreamReader(request.getInputStream(), "UTF-8"));

						StringBuilder sb = new StringBuilder();

						String inputStr;

						while ((inputStr = streamReader.readLine()) != null) {

							sb.append(inputStr);

						}

						Map<String, Object> map = new HashMap<String, Object>();

						if (sb.toString() != null) {
							String str = sb.toString().replaceAll("[\\{\\}\\[\\]]", "").replaceAll(":", "=")
									.replaceAll("\"", "");
							String[] strs = str.split(",");

							for (String string : strs) {

								String key = string.split("=")[0].trim();

								String value = string.split("=")[1];

								map.put(key, value);
							}
						}

						try {
							Object obj = Class.forName(paramCclazz.toString().replace("class ", "")).newInstance();

							Field[] fileds = obj.getClass().getDeclaredFields();

							for (int i = 0; i < fileds.length; i++) {

								fileds[i].setAccessible(true);

								if (fileds[i].getName() != "serialVersionUID") {

									if (fileds[i].getType() == String.class) {

										fileds[i].set(obj, map.get(fileds[i].getName()));

									} else if (fileds[i].getType().getName().equals("int")) {

										fileds[i].set(obj, Integer.valueOf(map.get(fileds[i].getName()).toString()));
									}
								}

							}

							args[args_i++] = obj;

						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}

			index++;

		}
		return args;

	}

}
