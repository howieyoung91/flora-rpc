/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.server.transport.support;

import xyz.yanghaoyu.flora.rpc.base.config.ServerConfig;
import xyz.yanghaoyu.flora.rpc.base.service.Service;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceHandler;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceRegistry;
import xyz.yanghaoyu.flora.rpc.server.transport.ServicePublisher;

import java.util.concurrent.Future;

public abstract class ServicePublishCapableRpcServer
        extends AbstractConfigurableRpcServer implements ServicePublisher {
    public ServicePublishCapableRpcServer(ServerConfig config, ServiceHandler handler) {
        super(config, handler);
    }

    // ========================================   public methods   =========================================

    /**
     * 发布服务
     * 先把 bean 全部注册 并不对外暴露
     */
    @Override
    public void publishService(Service service) {
        getRegistry().register(service);
    }

    /**
     * 服务下线
     */
    @Override
    public void cancelServices() {
        getRegistry().cancelServices();
    }

    // ========================================   public methods   =========================================


    // ========================================  override methods  =========================================

    @Override
    protected void afterStart(Future<? super Void> future) {
        finishPublish(); // 在服务器打开之后上线所有服务
    }

    @Override
    protected void beforeClose() {
        cancelServices(); // 在服务器关闭之前下线所有服务
    }

    // ========================================  override methods  =========================================


    // ========================================  private methods  ==========================================

    /**
     * 完成发布
     * 激活所有服务
     * 把服务全部暴露到注册中心
     */
    protected void finishPublish() {
        getRegistry().exposeServices(getConfiguration().address());
    }

    protected abstract ServiceRegistry getRegistry();

    // ========================================  private methods  =========================================
}
