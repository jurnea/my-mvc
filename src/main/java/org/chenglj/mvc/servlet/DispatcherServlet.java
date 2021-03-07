package org.chenglj.mvc.servlet;

import org.chenglj.mvc.annotation.Autowire;
import org.chenglj.mvc.annotation.Controller;
import org.chenglj.mvc.annotation.RequestMapping;
import org.chenglj.mvc.annotation.Service;
import org.chenglj.mvc.entity.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @Description 
 * @Date
 * @Author chenglj
 **/
public class DispatcherServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

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
                //获取所有的属性
                Class<?> beanClazz = entry.getValue().getClass();
                boolean controllerPresent = beanClazz.isAnnotationPresent(Controller.class);
                RequestMapping requestMappingAnnotation = beanClazz.getDeclaredAnnotation(RequestMapping.class);
                //既有Controller注解也有RequestMapping注解
                if(controllerPresent && requestMappingAnnotation != null){
                    //类上的注解@RequestMapping中的值
                    String classMappingValue = requestMappingAnnotation.value();
                    Method[] declaredMethods = beanClazz.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                        if(annotation == null){
                            continue;
                        }
                        //方法中@RequestMapping的值
                        String methodMappingValue = annotation.value();

                        String url = classMappingValue+methodMappingValue;
                        mappings.put(url,new Mapping(entry.getKey(),method));
                        logger.info("初始化[{}}] -> {}",url,method.getName());
                    }


                }
            }
        } catch (Exception e) {
            logger.error("add mapping error",e);
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

        logger.info("staring add bean dependency");
        try {
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                //获取所有的属性field
                Field[] fields = entry.getValue().getClass().getDeclaredFields();
                for (Field field : fields) {
                    Autowire annotation = field.getAnnotation(Autowire.class);
                    if(annotation != null){
                        field.setAccessible(true);
                        String className = field.getType().getName();
                        field.set(entry.getValue(),beans.get(className));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("add bean dependency error !",e);
        }
    }

    private void newInstance() {
        try {
            for (String className : classNames) {


                try {
                    Class<?> clazz = Class.forName(className);
                    if(clazz.isAnnotationPresent(Controller.class)
                            || clazz.isAnnotationPresent(Service.class)){
                        Object obj = clazz.newInstance();
                        beans.put(clazz.getName(),obj);
                        logger.info("init bean ：{}",clazz.getName());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            // 初始化bean完成后className销毁
            classNames.clear();
            classNames = null;
        } catch (Exception e) {
            logger.error("new instance error",e);
        }
    }

    //进行包扫描
    public void scanPackage(String backPackage) {
        logger.info("扫描包：backPackage->{}",backPackage);
        String path = DispatcherServlet.class.getClassLoader().getResource(backPackage).getPath();
        File filePath = new File(path);
        File[] files = filePath.listFiles();
        for (File file : files) {
            if(file.isDirectory()){
                scanPackage(backPackage+file.getName()+"/");
            } else {
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                //不是目录，加载完整类名 simpleName -> OrderController
                String simpleName = file.getName().substring(0,file.getName().lastIndexOf("."));
                String className = backPackage.replaceAll("\\/",".")+simpleName;
                classNames.add(className);

            }

        }
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
        logger.info("request url [{}]",url);
        Mapping mapping = mappings.get(url);
        if( mapping !=null){
            Object controllerObj = beans.get(mapping.getClassName());
            Method method = mapping.getMethod();
            try {
                Object invokeResult = method.invoke(controllerObj);
                resp.setContentType("application/json;charset=utf-8");
                resp.getWriter().write((String)invokeResult);
                logger.info("返回内容:{}",invokeResult);
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
