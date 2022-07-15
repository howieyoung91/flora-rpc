/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.service;

public interface ServiceKey {
    String group();

    String interfaceName();

    String version();

    String namespace();

    String serviceName();
}
