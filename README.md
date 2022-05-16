# flora-rpc

一个 rpc 框架，为 [flora-framework](https://github.com/howieyoung91/flora-framework) 提供了远程过程调用的能力
![flora-rpc](./assets/img/flora-rpc-structure.png)

0. RPC Server 把 Service 注册到注册中心
1. RPC 客户端请求注册中心
2. 注册中心返回 Service 所在的 Rpc Server 的地址
3. RPC 客户端请求 RPC Server
4. RPC Server 收到请求，调用请求目标方法
5. RPC Server 把调用结果返回给 RPC Client
6. RPC Client 收到响应，调用完毕

## Features

- [x] Zookeeper 管理服务
- [x] 自定义 rpc 通信协议
- [x] 心跳检测，长连接
- [x] 负载均衡，内置以下算法，支持自定义
  1. 随机算法
  2. 一致性哈希
- [x] 默认采用 Kryo 序列化，内置 json, protostuff, hessian 序列化，同时支持添加更多的序列化机制
- [x] 默认不使用压缩，内置 gzip，支持添加更多的压缩算法
- [x] 注解配置消费服务，发布服务
  1. `@RpcService`
  2. `@RpcRequest`
  3. `@RpcServiceReference`
  4. `@RpcResponse`

## TODO

- [ ] 多服务注册中心
- [ ] 服务监控

## Usage

以下代码完成了一个回声测试

#### 服务接口

Provider 端实现该接口，Consumer 端使用 rpc 调用到这个接口

```java
public interface Service {
    String echo(String str);
}
```

#### Provider / Server

##### 实现接口

```java
@Component
// 标记这是一个 rpc 服务
@RpcService(group = "xyz.yanghaoyu.rpc.test", version = "1.0")
public class DefaultService implements Service {
    @Override
    public String echo(String str) {
        return str;
    }
}
```

##### 完成配置

```java
@Configuration
// 开启自动配置，让 flora-rpc 的组件全部注入进来
@Enable.AutoConfiguration
// 扫描到服务的实现类
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.test.rpc.server.service")
public class ServerConfiguration {
    // 配置 zookeeper 的地址，也可以使用 yaml 配置
    @Bean
    public ZooKeeperConfigurer configurer() {
        return new ZooKeeperConfigurer() {
            @Override
            public String address() {
                return "127.0.0.1:2181";
            }
        };
    }
}
```

##### 启动 Provider

```java
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx
                = new AnnotationConfigApplicationContext(ServerConfiguration.class);
        // 从容器中获取到 server
        RpcServer rpcServer = ctx.getBean(RpcServerFactoryBean.BEAN_NAME, RpcServer.class);
        // 启动 server
        rpcServer.start();
    }
}
```

#### Consumer / Client

##### 实现消费者

```java
@Component
public class ServiceConsumer {
    @RpcServiceReference(group = "xyz.yanghaoyu.rpc.test", version = "1.0")
    private Service service;

    public String echo(String str) {
        return service.echo(str);
    }
}
```

##### 完成相关配置

```java
@Configuration
@Enable.AutoConfiguration
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.test.rpc.client.consumer")
@Enable.PropertySource(location = "classpath:application.yaml")
public class ClientConfig implements RpcClientConfigurer {
}
```

这里使用了 yaml 配置

```yaml
# application.yaml
flora:
  rpc:
    client:
      compressor: GZIP # 默认不会对数据进行压缩, 这里修改为使用 gzip 压缩
      discovery:
        zookeeper:
          load-balance: CONSISTENT_HASH # 使用一致性哈希实现负载均衡，默认采用 random
          address: 127.0.0.1:2181 # zookeeper 的地址
```

##### 启动客户端

```java
public class App {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx
                = new AnnotationConfigApplicationContext(ClientConfig.class);
        ServiceConsumer serviceConsumer = ctx.getBean("serviceConsumer", ServiceConsumer.class);
        System.out.println(serviceConsumer.echo("Hello world!"));
    }
}
```
