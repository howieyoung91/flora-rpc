/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration;

import xyz.yanghaoyu.flora.annotation.Configuration;
import xyz.yanghaoyu.flora.annotation.Enable;

@Configuration
@Enable.ComponentScan(basePackages = "xyz.yanghaoyu.flora.rpc.client.autoconfiguration")
public class FloraRpcClientAutoConfiguration {
}
