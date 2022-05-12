/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.builder;

import org.apache.curator.retry.ExponentialBackoffRetry;
import xyz.yanghaoyu.flora.rpc.base.exception.RpcClientException;
import xyz.yanghaoyu.flora.rpc.base.service.support.ZooKeeperBuilder;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ZooKeeperConfigProperties;
import xyz.yanghaoyu.flora.rpc.client.autoconfiguration.config.ZooKeeperConfigurer;

public class ZooKeeperBuilderFactory {
    private ZooKeeperConfigurer       configurer;
    private ZooKeeperConfigProperties properties;

    private ZooKeeperBuilderFactory(ZooKeeperConfigurer configurer, ZooKeeperConfigProperties properties) {
        this.configurer = configurer;
        this.properties = properties;
    }

    public static ZooKeeperBuilderFactory aZooKeeperConfigBuilder(ZooKeeperConfigurer configurer, ZooKeeperConfigProperties properties) {
        return new ZooKeeperBuilderFactory(configurer, properties);
    }

    public ZooKeeperBuilder build() {
        return ZooKeeperBuilder.aNewZooKeeper()
                .zookeeperAddress(getZooKeeperAddress())
                .retryPolicy(new ExponentialBackoffRetry(getBaseSleepTime(), getMaxRetries()));
    }

    public String getZooKeeperAddress() {
        String address = properties.getZooKeeperAddress();
        if (configurer != null) {
            String addressByConfigurer = configurer.getZooKeeperAddress();
            if (addressByConfigurer != null) {
                address = addressByConfigurer;
            }
        }
        if (address == null) {
            throw new RpcClientException("found no zookeeper address, it is required!");
        }
        return address;
    }

    public Integer getBaseSleepTime() {
        Integer baseSleepTime = properties.getBaseSleepTime();
        if (configurer != null) {
            Integer baseSleepTimeByConfigurer = configurer.getBaseSleepTime();
            if (baseSleepTimeByConfigurer != null) {
                baseSleepTime = baseSleepTimeByConfigurer;
            }
        }

        if (baseSleepTime == null) {
            baseSleepTime = 3000;
        }
        return baseSleepTime;
    }

    public Integer getMaxRetries() {
        Integer maxRetries = properties.getMaxRetries();
        if (configurer != null) {
            Integer maxRetriesByConfigurer = configurer.getMaxRetries();
            if (maxRetriesByConfigurer != null) {
                maxRetries = maxRetriesByConfigurer;
            }
        }

        if (maxRetries == null) {
            maxRetries = 3;
        }
        return maxRetries;
    }
}
