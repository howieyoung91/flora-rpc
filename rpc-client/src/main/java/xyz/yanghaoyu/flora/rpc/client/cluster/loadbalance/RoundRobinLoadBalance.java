package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.framework.core.context.ApplicationListener;
import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.event.RemoteService;
import xyz.yanghaoyu.flora.rpc.base.event.ServiceCanceledEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询算法
 *
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/17 11:50]
 */
public class RoundRobinLoadBalance extends AbstractServiceLoadBalance
        implements ApplicationListener<ServiceCanceledEvent> {
    @Override
    public String name() {
        return "ROUND";
    }

    private Map<String, AtomicInteger> sequences = new ConcurrentHashMap<>();

    @Override
    protected URL doSelect(Collection<URL> serverAddresses, Invocation invocation) {
        String        serviceName     = invocation.getServiceName();
        AtomicInteger currentSequence = sequences.computeIfAbsent(serviceName, s -> new AtomicInteger(-1));

        int size  = serverAddresses.size();
        int index = currentSequence.updateAndGet(sequence -> sequence == size - 1 ? 0 : ++sequence);

        if (serverAddresses instanceof List) {
            return ((List<URL>) serverAddresses).get(index);
        }
        else {
            Iterator<URL> it = serverAddresses.iterator();
            while (index-- == 0) {
                it.next();
            }
            return it.next();
        }
    }

    @Override
    public void onApplicationEvent(ServiceCanceledEvent event) {
        RemoteService service  = event.getService();
        AtomicInteger sequence = sequences.get(service.getName());
        sequence.decrementAndGet();
    }
}
