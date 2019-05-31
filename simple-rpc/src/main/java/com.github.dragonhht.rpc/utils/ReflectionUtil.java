package com.github.dragonhht.rpc.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * .
 *
 * @author: huang
 * @Date: 2019-5-31
 */
@Slf4j
public enum ReflectionUtil {
    /** 工具类实例. */
    INSTANCE;

    /**
     * 创建实例.
     * @param cls
     * @return
     */
    public Object newInstance(Class<?> cls) {
        Object instance = null;
        try {
            instance = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("类实例化失败", e);
        }
        return instance;
    }

    /**
     * 调用方法.
     * @param obj 对象
     * @param method 方法
     * @param args 参数
     * @return
     */
    public Object invokeMethod(Object obj, Method method, Object... args) {
        Object result = null;
        try {
            method.setAccessible(true);
            result = method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("方法调用失败", e);
        }
        return result;
    }

    public Object invokeMethod(Object obj, String method, Object... args) {
        Object result = null;
        try {
            for (Method declaredMethod : obj.getClass().getDeclaredMethods()) {
                if (declaredMethod.getName().equals(method)) {
                    declaredMethod.setAccessible(true);
                    result = declaredMethod.invoke(obj, args);
                    break;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("方法调用失败", e);
        }
        return result;
    }

    public Object invokeMethod(Object obj, String method, Class[] paramTypes, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = obj.getClass().getDeclaredMethod(method, paramTypes);
        return m.invoke(obj, args);
    }

    /**
     * 设置成员属性的值.
     * @param obj 对象
     * @param field 属性
     * @param value 值
     */
    public void setField(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("设置属性值失败", e);
        }
    }

    /**
     * 获取方法的参数信息.
     * @param method 方法
     * @return 参数信息
     */
    public Parameter[] getMethodParameters(Method method) {
        Parameter parameter = method.getParameters()[0];
        return method.getParameters();
    }

}
