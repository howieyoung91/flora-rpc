/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.service.discovery;

import xyz.yanghaoyu.flora.framework.core.beans.factory.ApplicationEventPublisherAware;
import xyz.yanghaoyu.flora.framework.core.context.ApplicationEventPublisher;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceNotFoundException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceDiscovery;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.base.transport.dto.RpcRequestConfig;
import xyz.yanghaoyu.flora.rpc.base.util.ServiceUtil;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * discovery 责任链
 */
public class DefaultServiceDiscoveryChain implements ServiceDiscovery, ApplicationEventPublisherAware {
    private List<ServiceDiscovery> discoveries = new LinkedList<>();

    private DefaultServiceDiscoveryChain() {}

    @Override
    public InetSocketAddress discover(RpcRequestConfig requestConfig) throws ServiceNotFoundException {
        for (ServiceDiscovery discovery : discoveries) {
            InetSocketAddress target = discovery.discover(requestConfig);
            if (target != null) {
                return target;
            }
        }
        return null;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        for (ServiceDiscovery discovery : discoveries) {
            if (discovery instanceof ApplicationEventPublisherAware) {
                ((ApplicationEventPublisherAware) discovery).setApplicationEventPublisher(publisher);
            }
        }
    }

    public static final class Builder {
        private List<ServiceDiscovery> discoveries = new LinkedList<>();
        private LocalServiceDiscovery  localServiceDiscovery;

        private Builder() {}

        public static Builder aChain() {
            return new Builder();
        }

        public Builder configureLocalServiceDiscovery(ServiceRegistry registry, int port) {
            localServiceDiscovery = new LocalServiceDiscovery(registry, ServiceUtil.getLocalAddress(port));
            return this;
        }

        public Builder addDiscovery(ServiceDiscovery discovery) {
            this.discoveries.add(discovery);
            return this;
        }

        public DefaultServiceDiscoveryChain build() {
            DefaultServiceDiscoveryChain chain = new DefaultServiceDiscoveryChain();
            if (localServiceDiscovery != null) {
                discoveries.add(0, localServiceDiscovery);
            }
            discoveries.add(DefaultServiceDiscovery.INSTANCE);
            chain.discoveries = discoveries;
            return chain;
        }
    }
}
