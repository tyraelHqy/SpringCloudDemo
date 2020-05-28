package com.tyrael.consul.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("tyrael-service-producer")
@Component
public interface FeignService {
    @RequestMapping(value = "/hellofeign")
    public String hello();
}
