package org.chenglj.mvc.entity;

import java.lang.reflect.Method;

/*
 * @Description 
 * @Date  
 * @Author chenglj
 **/
public class Mapping {

    private String className;

    private Method method;

    public Mapping(String className, Method method) {
        this.className = className;
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
