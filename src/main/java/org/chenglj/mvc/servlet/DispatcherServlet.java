package org.chenglj.mvc.servlet;

import org.chenglj.mvc.annotation.Autowire;
import org.chenglj.mvc.annotation.Controller;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static sun.security.pkcs11.wrapper.Functions.addMapping;

/*
 * @Description 
 * @Date
 * @Author chenglj
 **/
public class DispatcherServlet extends HttpServlet {


    List<String> classNames = new ArrayList<>();

    private Map<String,Object> beans = new ConcurrentHashMap();
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        scanPackage("");
        newInstance();
        addDependency();
        addMapping();

    }

    private void addMapping() {
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
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


}
