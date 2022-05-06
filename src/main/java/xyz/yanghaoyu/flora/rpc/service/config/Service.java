/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.config;

public class Service {
    private final ServiceConfig config;

    public Service(ServiceConfig config) {
        this.config = config;
    }

    public ServiceConfig getConfig() {
        return config;
    }
}
