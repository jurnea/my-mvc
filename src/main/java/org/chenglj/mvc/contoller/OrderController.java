package org.chenglj.mvc.contoller;

import org.chenglj.mvc.OrderService;
import org.chenglj.mvc.annotation.Autowire;
import org.chenglj.mvc.annotation.Controller;
import org.chenglj.mvc.annotation.RequestMapping;

import java.lang.reflect.Field;

/*
 * @Description 
 * @Date  
 * @Author chenglj
 **/
@Controller
@RequestMapping(value = "/order")
public class OrderController {

    @Autowire
    private OrderService orderService;


    @RequestMapping(value = "/query")
    public Object query(){
        System.out.println("反射调用成功");
        return "查询成功!success";
    }


}
