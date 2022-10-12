package xyz.yanghaoyu.flora.rpc.client.autoconfiguration.support;

import xyz.yanghaoyu.flora.framework.annotation.Component;
import xyz.yanghaoyu.flora.framework.core.beans.factory.BeanFactory;
import xyz.yanghaoyu.flora.framework.core.beans.factory.BeanFactoryAware;
import xyz.yanghaoyu.flora.framework.core.beans.factory.ConfigurableListableBeanFactory;
import xyz.yanghaoyu.flora.framework.exception.BeansException;
import xyz.yanghaoyu.flora.rpc.base.exception.ServiceKeyException;
import xyz.yanghaoyu.flora.rpc.base.service.ServiceKey;
import xyz.yanghaoyu.flora.rpc.base.service.key.AggregativeServiceKeyFactory;
import xyz.yanghaoyu.flora.rpc.base.service.key.ConfigurableServiceKeyFactory;
import xyz.yanghaoyu.flora.rpc.base.service.key.ServiceKeyFactory;
import xyz.yanghaoyu.flora.rpc.base.service.support.zookeeper.ZookeeperServiceKeyFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class DefaultServiceKeyFactory implements BeanFactoryAware, AggregativeServiceKeyFactory {
    private Map<String, ServiceKeyFactory> factories = new HashMap<>();

    @Override
    public String getName() {
        return "flora-rpc-client$DefaultServiceKeyFactory$";
    }

    @Override
    public Map<String, ServiceKeyFactory> getFactories() {
        return factories;
    }

    @Override
    public ServiceKeyFactory getFactory(String factoryName) {
        return getFactories().get(factoryName);
    }

    @Override
    public ServiceKey createDefaultKey(String interfaceName) {
        ServiceKeyFactory factory = getFactory(ZookeeperServiceKeyFactory.NAME);
        Objects.requireNonNull(factory, "Unknown service key factory " + ZookeeperServiceKeyFactory.NAME);
        return factory.createDefaultKey(interfaceName);
    }

    public void addFactory(String name, ServiceKeyFactory factory) {
        Objects.requireNonNull(factory, "Cannot add null into ServiceKeyFactory");
        if (factory == this) {
            throw new UnsupportedOperationException();
        }
        if (factories.containsKey(name)) {
            throw new ServiceKeyException("duplicate factory [" + name + "]");
        }
        factories.put(name, factory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        ConfigurableListableBeanFactory           factory = (ConfigurableListableBeanFactory) beanFactory;
        Collection<ConfigurableServiceKeyFactory> beans   = factory.getBeansOfType(ConfigurableServiceKeyFactory.class).values();
        for (ConfigurableServiceKeyFactory bean : beans) {
            factories.put(bean.getName(), bean);
        }
    }
}
