package xyz.yanghaoyu.flora.rpc.base.service.key;

import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/12 10:23]
 */
public abstract class ConfigurableServiceKeyFactory implements ServiceKeyFactory {
    protected ClientConfig clientConfig;

    public ConfigurableServiceKeyFactory(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }
}
