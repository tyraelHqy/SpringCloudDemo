# SpringCloud consul zuul demo 

该项目为熟悉SpringCloud各个组件的作用所创建，Spring Cloud 使用的各种示例

项目为父子项目，共有2个服务提供端，一个服务消费者，一个网关进行管理，服务注册与管理是基于Consul。
- spring-cloud-gateway-zuul
- spring-cloud-consul-producer
- spring-cloud-consul-producer-2
- spring-cloud-consul-consumer

## spring-cloud-gateway-zuul(8888)
建立网关,将所有的访问/consumer/** 的url直接重定向到http://localhost:8603，讲所有访问/producer/** 的url通过服务化去consul中获取服务名字为tyrael-service-producer的服务。

自己建立Token过滤器进行验证url中有没有携带token。没有携带将返回错误代码。

## spring-cloud-consul-producer(8601与8602)
创建hello与helloFeign接口，返回字符串。

## spring-cloud-consul-consumer(8603)
- 获取所有的服务getService接口
- 轮询的选择同服务(来自不同的客户注册中心,IP不同)chooseService接口
- 使用 RestTemplate 进行远程调用call接口
- ribbon调用ribbon-call接口
- feign调用feign-call接口
