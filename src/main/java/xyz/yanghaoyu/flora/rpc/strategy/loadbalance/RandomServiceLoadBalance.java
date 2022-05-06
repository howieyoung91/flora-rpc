/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.strategy.loadbalance;

import xyz.yanghaoyu.flora.rpc.transport.dto.Request;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomServiceLoadBalance extends AbstractServiceLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, Request request) {
        int randomIndex = ThreadLocalRandom.current().nextInt(serviceAddresses.size());
        return serviceAddresses.get(randomIndex);
    }
}
