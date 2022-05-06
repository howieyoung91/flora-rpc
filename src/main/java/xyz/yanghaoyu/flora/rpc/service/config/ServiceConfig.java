/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.service.config;

public class ServiceConfig {
    private final String group;
    private final String version;

    public ServiceConfig(String group, String version) {
        this.group = group;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getVersion() {
        return version;
    }
}
