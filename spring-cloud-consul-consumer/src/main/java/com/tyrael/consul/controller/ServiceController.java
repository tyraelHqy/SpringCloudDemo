package com.tyrael.consul.controller;

import com.tyrael.consul.service.FeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServiceController {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);


    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private DiscoveryClient discoveryClient;


    String SERVICE_ID = "tyrael-service-producer";

    /**
     * 获取所有的服务
     *
     * @return
     */
    @RequestMapping("/getServices")
    public Object services() {
        return discoveryClient.getInstances(SERVICE_ID);
    }

    /**
     * // 轮询的选择同服务(来自不同的客户注册中心,IP不同)
     *
     * @return
     */
    @RequestMapping("/chooseService")
    public Object discover() {
        return loadBalancerClient.choose(SERVICE_ID).getUri().toString();
    }

    @RequestMapping("/hello")
    public String hello() {
        String name = "tyrael";
        return "Hello " + name + " consul consumer";
    }


}
