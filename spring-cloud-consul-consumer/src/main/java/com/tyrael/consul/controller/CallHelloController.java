package com.tyrael.consul.controller;

import com.tyrael.consul.service.FeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CallHelloController {

    private static final Logger LOG = LoggerFactory.getLogger(CallHelloController.class);

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FeignService feignService;

    String SERVICE_ID = "tyrael-service-producer";

    /**
     * 使用 RestTemplate 进行远程调用
     * @return
     */
    @RequestMapping("/call")
    public String call(){
        ServiceInstance serviceInstance = loadBalancer.choose(SERVICE_ID);
        LOG.info("服务地址：" + serviceInstance.getUri());
        LOG.info("服务名称：" + serviceInstance.getServiceId());

        String callServiceResult = new RestTemplate().getForObject(serviceInstance.getUri().toString() + "/hello" + "?name=tyyt&token=11",String.class);
        LOG.info(callServiceResult);
        return callServiceResult;
    }

    /**
     * ribbon
     */
    @RequestMapping("/ribbon-call")
    public String ribbonCall(){
        String method = "hello";
        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://" + SERVICE_ID + "/" + method +"?name=tyrael", String.class);
        LOG.info("使用ribbon调用状态码："+ forEntity.getStatusCode());
        LOG.info("使用ribbon调用内容："+ forEntity.getBody());

        return forEntity.getBody();
    }

    /**
     * feign
     */
    @RequestMapping("/feign-call")
    public String feignCall(){
        String result = "使用Feign调用: "+ feignService.hello();
        LOG.info(result);
        return result;
    }
}
