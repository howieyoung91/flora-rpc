package xyz.yanghaoyu.flora.rpc.service;

public interface ServiceRegistry {
    void register(Object service);

    Object getService(String serviceName);
}
