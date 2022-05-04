package xyz.yanghaoyu.flora.rpc.service.support;

import xyz.yanghaoyu.flora.rpc.exception.RpcException;
import xyz.yanghaoyu.flora.rpc.service.ServiceRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void register(Object service) {
        String serviceName = service.getClass().getCanonicalName();
        if (serviceMap.containsKey(serviceName)) {
            return;
        }

        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0) {
            throw new RpcException("找不到任何接口");
        }

        for (Class<?> i : interfaces) {
            serviceMap.put(i.getCanonicalName(), service);
        }
    }

    @Override
    public Object getService(String serviceName) {
        return null;
    }
}
