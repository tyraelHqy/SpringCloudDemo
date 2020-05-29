# SpringCloud consul zuul demo 

该项目为熟悉SpringCloud各个组件的作用所创建，Spring Cloud 使用的各种示例

项目为父子项目，共有2个服务提供端，一个服务消费者，一个网关进行管理，服务注册与管理是基于Consul。
- spring-cloud-gateway-zuul
- spring-cloud-consul-producer
- spring-cloud-consul-producer-2
- spring-cloud-consul-consumer

## spring-cloud-gateway-zuul(8888)
建立网关,将所有的访问/consumer/** 的url直接重定向到http://localhost:8603，讲所有访问/producer/** 的url通过服务化去consul中获取服务名字为tyrael-service-producer的服务。

通过url映射的方式来实现zuul的转发有局限性，比如每增加一个服务就需要配置一条内容，另外后端的服务如果是动态来提供，就不能采用这种方案来配置了。
实际上在实现微服务架构时，服务名与服务实例地址的关系在consul server中已经存在了，所以只需要将Zuul注册到consul上去发现其他服务，就可以实现对serviceId的映射。

### Zuul的核心Filter
Filter是Zuul的核心，用来实现对外服务的控制。Filter的生命周期有4个，分别是“PRE”、“ROUTING”、“POST”、“ERROR”。

Zuul大部分功能都是通过过滤器来实现的，这些过滤器类型对应于请求的典型生命周期。

- **PRE：** 这种过滤器在请求被路由之前调用。我们可利用这种过滤器实现身份验证、在集群中选择请求的微服务、记录调试信息等。
- **ROUTING：** 这种过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Netfilx Ribbon请求微服务。
- **POST：** 这种过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的HTTP Header、收集统计信息和指标、将响应从微服务发送给客户端等。
- **ERROR：** 在其他阶段发生错误时执行该过滤器。 除了默认的过滤器类型，Zuul还允许我们创建自定义的过滤器类型。例如，我们可以定制一种STATIC类型的过滤器，直接在Zuul中生成响应，而不将请求转发到后端的微服务。

demo实现：因为服务网关应对的是外部的所有请求，为了避免产生安全隐患，我们需要对请求做一定的限制，比如**请求中含有Token便让请求继续往下走** ，如果请求不带Token就直接返回并给出提示。

测试：

访问地址：http://localhost:8888/producer/hello?token=11&name=tyrael

返回：Hello tyrael consul 2

访问地址：http://localhost:8888/producer/hello?&name=tyrael

返回：token is empty

### 路由熔断
当我们的后端服务出现异常的时候，我们不希望将异常抛出给最外层，期望服务可以自动进行一降级。Zuul给我们提供了这样的支持。当某个服务出现异常时，直接返回我们预设的信息。

我们通过自定义的fallback方法，并且将其指定给某个route来实现该route访问出问题的熔断处理。主要继承ZuulFallbackProvider接口来实现，ZuulFallbackProvider默认有两个方法，一个用来指明熔断拦截哪个服务，一个定制返回内容。

## spring-cloud-consul-producer(8601与8602)
创建hello与helloFeign接口，返回字符串。

## spring-cloud-consul-consumer(8603)
- 获取所有的服务getService接口
- 轮询的选择同服务(来自不同的客户注册中心,IP不同)chooseService接口
- 使用 RestTemplate 进行远程调用call接口
- ribbon调用ribbon-call接口
- feign调用feign-call接口

## 注册中心Consul使用
**Consul 的优势：**

- 使用 Raft 算法来保证一致性, 比复杂的 Paxos 算法更直接. 相比较而言, zookeeper 采用的是 Paxos, 而 etcd 使用的则是 Raft。
- 支持多数据中心，内外网的服务采用不同的端口进行监听。 多数据中心集群可以避免单数据中心的单点故障,而其部署则需要考虑网络延迟, 分片等情况等。 zookeeper 和 etcd 均不提供多数据中心功能的支持。
- 支持健康检查。 etcd 不提供此功能。
- 支持 http 和 dns 协议接口。 zookeeper 的集成较为复杂, etcd 只支持 http 协议。
- 官方提供 web 管理界面, etcd 无此功能。
- 综合比较, Consul 作为服务注册和配置管理的新星, 比较值得关注和研究。

**特性：**

- 服务发现
- 健康检查
- Key/Value 存储
- 多数据中心

**Consul 角色**

- client: 客户端, 无状态, 将 HTTP 和 DNS 接口请求转发给局域网内的服务端集群。
- server: 服务端, 保存配置信息, 高可用集群, 在局域网内与本地客户端通讯, 通过广域网与其它数据中心通讯。 每个数据中心的 server 数量推荐为 3 个或是 5 个。

Consul 客户端、服务端还支持跨中心的使用，更加提高了它的高可用性。


**Consul 工作原理：**

- 1、当 Producer 启动的时候，会向 Consul 发送一个 post 请求，告诉 Consul 自己的 IP 和 Port
- 2、Consul 接收到 Producer 的注册后，每隔10s（默认）会向 Producer 发送一个健康检查的请求，检验Producer是否健康
- 3、当 Consumer 发送 GET 方式请求 /api/address 到 Producer 时，会先从 Consul 中拿到一个存储服务 IP 和 Port 的临时表，从表中拿到 Producer 的 IP 和 Port 后再发送 GET 方式请求 /api/address
- 4、该临时表每隔10s会更新，只包含有通过了健康检查的 Producer

Spring Cloud Consul 项目是针对 Consul 的服务治理实现。Consul 是一个分布式高可用的系统，它包含多个组件，但是作为一个整体，在微服务架构中为我们的基础设施提供服务发现和服务配置的工具。

## Spring Cloud Feign
Spring Cloud Feign是一套基于Netflix Feign实现的声明式服务调用客户端。它使得编写Web服务客户端变得更加简单。我们只需要通过创建接口并用注解来配置它既可完成对Web服务接口的绑定。它具备可插拔的注解支持，包括Feign注解、JAX-RS注解。它也支持可插拔的编码器和解码器。

Spring Cloud Feign还扩展了对Spring MVC注解的支持，同时还整合了Ribbon和Eureka来提供均衡负载的HTTP客户端实现。

Feign便可以理解为一种http框架，用于分布式服务之间通过Http进行接口交互。说他是框架，有点过了，可以理解为一个http工具，只不过在spring cloud全家桶的体系中，它比httpclient，okhttp，retrofit这些http工具都要强大的多。

服务提供方使用的是一个RestController暴露计算服务，服务消费方使用http工具（Feign）进行远程调用，这再清晰不过了，也是符合软件设计的，因为Feign接口的定义是存在于消费方，所以是真正的松耦合。

### Feign注意事项
- 当接口定义中出现了实体类时，需要使用@RequestBody注解。多个实体类，则需要用一个大的vo对其进行包裹，要时刻记住，Feign接口最终是会转换成一次http请求。

- 接口定义中的注解和实现类中的注解要分别写一次，不能继承。

- Feign调用一般配合eureka等注册中心使用，并且在客户端可以支持Hystrix机制，本文为了讲解共享接口这一设计，所以重心放在了Feign上，实际开发中，这些spring cloud的其他组件通常配套使用。

- 对http深入理解，在使用Feign时可以事半功倍。

### 总结：Feign的源码实现的过程如下：
    
- 首先通过@EnableFeignCleints注解开启FeignCleint

- 根据Feign的规则实现接口，并加@FeignCleint注解

- 程序启动后，会进行包扫描，扫描所有的@FeignCleint的注解的类，并将这些信息注入到ioc容器中。

- 当接口的方法被调用，通过jdk的代理，来生成具体的RequestTemplate

- RequestTemplate在生成Request

- Request交给Client去处理，其中Client可以是HttpUrlConnection、HttpClient也可以是Okhttp

- 最后Client被封装到LoadBalanceClient类，这个类结合类Ribbon做到了负载均衡。
