package org.chenglj.mvc.servlet;

import org.chenglj.mvc.annotation.Autowire;
import org.chenglj.mvc.annotation.Controller;
import org.chenglj.mvc.annotation.RequestMapping;
import org.chenglj.mvc.entity.Mapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @Description 
 * @Date
 * @Author chenglj
 **/
public class DispatcherServlet extends HttpServlet {


    List<String> classNames = new ArrayList<>();

    // full className : instance
    private Map<String,Object> beans = new ConcurrentHashMap();

    private Map<String, Mapping> mappings = new ConcurrentHashMap<>();
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        scanPackage("");
        newInstance();
        addDependency();
        addMapping();

    }

    private void addMapping() {
        try {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                //获取所有的属性，父类？
                Class<?> beanClazz = entry.getValue().getClass();
                boolean controllerPresent = beanClazz.isAnnotationPresent(Controller.class);
                RequestMapping requestMappingAnnotation = beanClazz.getDeclaredAnnotation(RequestMapping.class);
                //既有Controller注解也有RequestMapping注解
                if(controllerPresent && requestMappingAnnotation != null){
                    //类上的注解内容
                    String classMappingValue = requestMappingAnnotation.value();
                    Method[] declaredFields = beanClazz.getDeclaredMethods();
                    for (Method declaredField : declaredFields) {
                        RequestMapping annotation = declaredField.getAnnotation(RequestMapping.class);
                        if(annotation == null){
                            continue;
                        }
                        String methodMappingValue = annotation.value();

                        String url = classMappingValue+methodMappingValue;
                        mappings.put(url,new Mapping(entry.getKey(),declaredField));
                        System.out.println("初始化["+url+"] ->"+declaredField.getName());
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new DispatcherServlet().init(null);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
    private void addDependency() {

        System.out.println("add==");
        try {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                //获取所有的属性，父类？
                Field[] fields = entry.getValue().getClass().getDeclaredFields();
                System.out.println(fields);
                for (Field field : fields) {
                    Autowire annotation = field.getAnnotation(Autowire.class);
                    if(annotation != null){
                        field.setAccessible(true);
                        Class<? extends Field> aClass = field.getClass();
                        System.out.println(aClass);
                        field.set(entry.getValue(),beans.get(aClass.getName()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void newInstance() {
        try {
            for (String className : classNames) {

                Class<?> clazz = Class.forName(className);
                Controller annotation = clazz.getAnnotation(Controller.class);
                if(annotation != null){
                    Object obj = clazz.newInstance();
                    beans.put(clazz.getName(),obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //进行包扫描
    public String scanPackage(String backPackage){
        System.out.println("backPackage:"+backPackage);
        String path = DispatcherServlet.class.getClassLoader().getResource(backPackage).getPath();
        File filePath = new File(path);
        File[] files = filePath.listFiles();
        for (File file : files) {
            System.out.println(file.getName()+"--"+file.getAbsoluteFile());
            if(file.isDirectory()){
                scanPackage(backPackage+file.getName()+"/");

            } else {
                //不是目录，加载完整类名
                String name = file.getName().substring(0,file.getName().lastIndexOf("."));
                String className = backPackage.replaceAll("\\/",".")+name;
                System.out.println(className);
                classNames.add(className);
            }

        }
        return null;
    }




    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String url = requestURI.replaceAll(contextPath, "");
        Mapping mapping = mappings.get(url);
        if( mapping !=null){
            Object controllerObj = beans.get(mapping.getClassName());
            Method method = mapping.getMethod();
            try {
                Object invokeResult = method.invoke(controllerObj);
                resp.setContentType("application/json;charset=utf-8");
                resp.getWriter().write((String)invokeResult);
                System.out.println(invokeResult);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


}
