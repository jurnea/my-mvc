package org.chenglj.mvc.contoller;

import org.chenglj.mvc.OrderService;
import org.chenglj.mvc.annotation.Autowire;
import org.chenglj.mvc.annotation.Controller;

import java.lang.reflect.Field;

/*
 * @Description 
 * @Date  
 * @Author chenglj
 **/
@Controller
public class OrderController {

    @Autowire
    private OrderService orderService;

    public static void main(String[] args) {
        Field[] fields = OrderController.class.getDeclaredFields();
        System.out.println(fields.length);
    }
}
