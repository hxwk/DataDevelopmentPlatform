package com.dfssi.dataplatform.vehicleinfo.cvvehiclenetworkbasicinfo.model;

import java.lang.reflect.Method;

public class ClassUtil {
	/**
	 * 调用对象obj的方法methodName, 并传入参数params
	 * 
	 * @param methodName
	 *            方法名称
	 * @param obj
	 *            对象
	 * @param params
	 *            参数
	 * @return
	 */
	public static Object invokeMethod(String methodName, Object obj,
			Object[] params) {
		Class clazz = obj.getClass();
		Class[] paramTypes = null;
		if (params != null && params.length > 0) {
			int paramsLen = params.length;
			paramTypes = new Class[paramsLen];
			paramTypes[0] = params[0].getClass();
			for (int i = 1; i < paramsLen; i++) {
				paramTypes[1] = params[i].getClass();
			}
		}

		Object result = null;
		Method method = null;
		try {
			method = clazz.getMethod(methodName, paramTypes);
			result = method.invoke(obj, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}
