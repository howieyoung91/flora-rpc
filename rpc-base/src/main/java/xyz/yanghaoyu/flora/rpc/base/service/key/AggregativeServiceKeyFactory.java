package xyz.yanghaoyu.flora.rpc.base.service.key;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;

import java.util.Map;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/12 10:36]
 */
public interface AggregativeServiceKeyFactory extends ServiceKeyFactory {
    Map<String, ServiceKeyFactory> getFactories();

    ServiceKeyFactory getFactory(String factoryName);

    default ServiceKey createDefaultKey(String factoryName, String interfaceName) {
        return getFactory(factoryName).createDefaultKey(interfaceName);
    }
}
