package org.chenglj.mvc.servlet;


import org.junit.Test;

import javax.servlet.ServletException;

public class DispatcherServletTest {

    DispatcherServlet dispatcherServlet = new DispatcherServlet();

    @Test
    public void test(){
        ///D:/project/my-mvc/target/test-classes/
        System.out.println(this.getClass().getResource("/").getPath());

        System.out.println(this.getClass().getResource("/org").getPath());
        ///D:/project/my-mvc/target/test-classes/org/chenglj
        System.out.println(this.getClass().getResource("/org/chenglj").getPath());
        System.out.println(this.getClass().getClassLoader().getResource(""));//file:/D:/project/my-mvc/target/test-classes/
        System.out.println(this.getClass().getClassLoader().getResource("/"));//null
        System.out.println(this.getClass().getClassLoader().getResource("/org"));//null
        System.out.println(this.getClass().getClassLoader().getResource("org")); //file:/D:/project/my-mvc/target/test-classes/org
        System.out.println(this.getClass().getClassLoader().getResource("org/")); //file:/D:/project/my-mvc/target/test-classes/org
        System.out.println(this.getClass().getClassLoader().getResource("org/chenglj/")); //file:/D:/project/my-mvc/target/test-classes/org/chenglj/




    }
    @Test
    public void init() {
        try {
            dispatcherServlet.init(null);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}