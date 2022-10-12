package xyz.yanghaoyu.flora.rpc.base.service.key;

import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/09 21:35]
 */
public interface ServiceKeyFactory {
    ServiceKey createDefaultKey(String interfaceName);

    String getName();
}
