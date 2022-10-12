package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.support;

import xyz.yanghaoyu.flora.framework.annotation.Bean;
import xyz.yanghaoyu.flora.framework.annotation.Configuration;
import xyz.yanghaoyu.flora.framework.annotation.Inject;
import xyz.yanghaoyu.flora.framework.annotation.Scope;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.client.service.generic.GenericRpcServiceReference;
import xyz.yanghaoyu.flora.rpc.client.transport.RpcClient;

/**
 * 支持泛化调用
 *
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/09 00:27]
 */
@Configuration("flora-rpc-client$GenericRpcServiceInvokeAutoConfiguration$")
public class GenericRpcServiceInvokeAutoConfiguration {
    @Bean("flora-rpc-client$GenericRpcServiceReference$")
    @Scope.Prototype
    public GenericRpcServiceReference genericRpcServiceReference(
            @Inject.ByName("flora-rpc-client$RpcClient$")
            RpcClient client,
            @Inject.ByName("flora-rpc-client$ServiceDiscovery$")
            ServiceDiscovery discovery
    ) {
        return new GenericRpcServiceReference(client, discovery);
    }
}
