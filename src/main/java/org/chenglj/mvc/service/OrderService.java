package org.chenglj.mvc.service;

import org.chenglj.mvc.annotation.Controller;
import org.chenglj.mvc.annotation.Service;

/*
 * @Description 
 * @Date  
 * @Author chenglj
 **/
@Service
public class OrderService {

    public String queryOrder(){
        return "order service 订单查询成功...";
    }
}
