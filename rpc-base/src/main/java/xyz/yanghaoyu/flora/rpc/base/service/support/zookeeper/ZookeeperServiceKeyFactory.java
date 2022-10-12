package xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper;

import xyz.yanghaoyu.flora.rpc.base.config.ClientConfig;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;
import xyz.yanghaoyu.flora.rpc.base.service.key.ConfigurableServiceKeyFactory;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/09 21:36]
 * todo
 */
public class ZookeeperServiceKeyFactory extends ConfigurableServiceKeyFactory {
    public static final String NAME = "Zookeeper";

    public ZookeeperServiceKeyFactory(ClientConfig clientConfig) {
        super(clientConfig);
    }

    @Override
    public ServiceKey createDefaultKey(String interfaceName) {
        return ZookeeperServiceKeyBuilder.aKey()
                .namespace(clientConfig.namespace())
                .version(clientConfig.version())
                .group(clientConfig.group())
                .interfaceName(interfaceName)
                .build();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
