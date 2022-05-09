/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service.config;

public class Service {
    private final Object        serviceBean;
    private final ServiceConfig config;

    public Service(Object serviceBean, ServiceConfig config) {
        this.serviceBean = serviceBean;
        this.config = config;
    }

    public Object getServiceBean() {
        return serviceBean;
    }

    public ServiceConfig getConfig() {
        return config;
    }
}
