/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.base.strategy.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomServiceLoadBalance extends AbstractServiceLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses) {
        int randomIndex = ThreadLocalRandom.current().nextInt(serviceAddresses.size());
        return serviceAddresses.get(randomIndex);
    }
}
