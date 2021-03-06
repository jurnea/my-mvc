package org.chenglj.mvc.contoller;

import org.chenglj.mvc.service.OrderService;
import org.chenglj.mvc.annotation.Autowire;
import org.chenglj.mvc.annotation.Controller;
import org.chenglj.mvc.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @Description 
 * @Date  
 * @Author chenglj
 **/
@Controller
@RequestMapping(value = "/order")
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowire
    private OrderService orderService;


    @RequestMapping(value = "/query")
    public Object query(){
        logger.info("request url invoked controller method");
        String result = orderService.queryOrder();
        return result;
    }


}
