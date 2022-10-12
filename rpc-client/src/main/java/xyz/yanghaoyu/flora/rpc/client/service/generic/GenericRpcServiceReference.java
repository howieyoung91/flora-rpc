package xyz.yanghaoyu.flora.rpc.client.service.generic;

import xyz.yanghaoyu.flora.rpc.base.annotation.RpcRequestAttribute;
import xyz.yanghaoyu.flora.rpc.base.annotation.ServiceReferenceAttribute;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.DefaultRpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcResponseBody;
import xyz.yanghaoyu.flora.rpc.client.annotation.RpcRequest;
import xyz.yanghaoyu.flora.rpc.client.service.GenericService;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/07 23:44]
 */
public class GenericRpcServiceReference implements GenericService {
    private RpcClient        client; // must be started
    private ServiceDiscovery discovery;

    public GenericRpcServiceReference(RpcClient client, ServiceDiscovery discovery) {
        this.client = client;
        this.discovery = discovery;
    }

    @Override
    public Object $invoke(ServiceReferenceAttribute referenceAttribute, String methodName, Class[] paramTypes, String[] args, RpcRequestAttribute requestAttribute) {
        RpcRequestConfig requestConfig = DefaultRpcRequestConfig.Builder.aRpcRequestConfig()
                .methodName(methodName)
                .paramTypes(paramTypes)
                .args(args)
                .rpcRequestAttribute(requestAttribute)
                .serviceReferenceAttribute(referenceAttribute)
                .build();

        InetSocketAddress target = null;
        try {
            target = discovery.discover(requestConfig);
            CompletableFuture<RpcResponseBody> promise  = client.send(requestConfig, target);
            RpcResponseBody                    respBody = promise.get();
            return respBody.getData();
        }
        catch (ServiceNotFoundException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object $invoke(ServiceReferenceAttribute referenceAttribute, String methodName, Class[] paramTypes, String... args) {
        Objects.requireNonNull(referenceAttribute.getServiceName());

        RpcRequestAttribute requestAttribute = new RpcRequestAttribute();
        requestAttribute.setAlwaysRemote(RpcRequest.DEFAULT_ALWAYS_REMOTE);

        return this.$invoke(referenceAttribute, methodName, paramTypes, args, requestAttribute);
    }

    @Override
    public Object $invoke(ServiceKey serviceKey, String methodName, Class[] paramTypes, String... args) {
        Objects.requireNonNull(serviceKey.serviceName());
        return this.$invoke(new ServiceReferenceAttribute(serviceKey),
                methodName, paramTypes, args, null);
    }
}
