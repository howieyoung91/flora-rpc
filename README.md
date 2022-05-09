# flora-rpc

一个 rpc 框架，可以单独使用，也可以使用 `flora-mate-rpc` 与 `flora-framework` 集成使用。

### flora-rpc rpc 通信协议

```
  0       4         5              6                7               8    11      15   (16B)
  +-------+---------+--------------+----------------+---------------+----+--------+
  | magic | version | message type | serialize type | compress type | id | length |
  +-------------------------------------------------------------------------------+
  |                                     body                                      |
  +-------------------------------------------------------------------------------+

  1. magic              魔数            用于快速判断是否是无效包
  2. version            版本号          用于协议升级
  3. message type       消息类型        可能是请求包，响应包，心跳检测请求包，心跳检测响应包
  4. serialize type     序列化类型
  5. compress type      body 压缩类型
  6. id                 报文 id
  7. length             报文长度
  8. body               报文数据
```

目前还在开发中...

相关技术：

1. netty
2. kyro 序列化
3. zookeeper，nacos
4. 负载均衡
5. 心跳检测，长连接
6. 服务监控