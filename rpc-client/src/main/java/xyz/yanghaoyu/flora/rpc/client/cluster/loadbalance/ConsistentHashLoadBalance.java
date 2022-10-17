/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.framework.core.context.ApplicationListener;
import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;
import xyz.yanghaoyu.flora.rpc.base.event.RemoteService;
import xyz.yanghaoyu.flora.rpc.base.event.ServiceCanceledEvent;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConsistentHashLoadBalance extends AbstractServiceLoadBalance
        implements ApplicationListener<ServiceCanceledEvent> {
    public static final String NAME = "CONSISTENT_HASH";

    private final Map<String, ConsistentHashCircle> circles = new ConcurrentHashMap<>();
    private       Object[]                          NULL    = new Object[0];

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void onApplicationEvent(ServiceCanceledEvent event) {
        RemoteService service = event.getService();
        removeServiceCache(service.getName(), service.getUrls());
    }

    @Override
    protected URL doSelect(Collection<URL> serverAddresses, Invocation invocation) {
        String               serviceName = invocation.getServiceName();
        ConsistentHashCircle circle      = circles.get(serviceName);
        int                  hashcode    = System.identityHashCode(serverAddresses);
        if (circle == null || !circle.validate(hashcode)) {
            circle = addServiceCache(serverAddresses, serviceName, hashcode);
        }

        String key = buildKey(serviceName, invocation.getArguments());
        return circle.select(key);
    }

    private ConsistentHashCircle addServiceCache(Collection<URL> urls, String serviceName, int hashcode) {
        circles.put(serviceName, new ConsistentHashCircle(urls, 10, hashcode));
        // 这里要先 put 再 get, 最大限度保证数据一致, 因为当前线程 put 完之后，其他线程可能覆盖掉数据
        return circles.get(serviceName);
    }

    private void removeServiceCache(String serviceName, Collection<URL> urls) {
        ConsistentHashCircle circle = circles.get(serviceName);
        if (circle != null) {
            circle.removeUrl(urls, 10);
        }
    }

    private String buildKey(String serviceName, Object[] args) {
        return serviceName + Arrays.hashCode(Arrays.stream(args == null ? NULL : args).toArray());
    }

    /**
     * 哈希环
     */
    private static class ConsistentHashCircle {
        private final TreeMap<Long, URL> virtualInvokers = new TreeMap<>();
        private final long               hashcode;

        private ConsistentHashCircle(Collection<URL> urls, int replicaNumber, int hashCode) {
            this.hashcode = hashCode;
            mapUrls(urls, replicaNumber);
        }

        /**
         * 把每个 Provider 的 url 映射到哈希环上
         */
        private void mapUrls(Collection<URL> urls, int replicaNumber) {
            forEachUrl(urls, replicaNumber, url -> digest -> hashcode -> {
                virtualInvokers.put(hashcode, url);
            });
        }

        private void removeUrl(Collection<URL> urls, int replicaNumber) {
            forEachUrl(urls, replicaNumber, url -> digest -> hashcode -> {
                virtualInvokers.remove(hashcode);
            });
        }

        private void forEachUrl(Collection<URL> urls, int replicaNumber,
                                Function<URL, Function<byte[], Consumer<Long>>> f) {
            for (URL url : urls) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(url.getAddress() + i);
                    for (int j = 0; j < 4; j++) {
                        long h = hash(digest, j);
                        f.apply(url).apply(digest).accept(h);
                    }
                }
            }
        }

        private URL select(String key) {
            long hash = hash(md5(key), 0);
            return selectForKey(hash);
        }

        private boolean validate(long hashcode) {
            return hashcode == this.hashcode;
        }

        private static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24
                    | (long) (digest[2 + idx * 4] & 255) << 16
                    | (long) (digest[1 + idx * 4] & 255) << 8
                    | (long) (digest[idx * 4] & 255))
                   & 4294967295L;
        }

        private URL selectForKey(long hashCode) {
            Map.Entry<Long, URL> entry =
                    virtualInvokers.tailMap(hashCode, true).firstEntry(); // 获取大于等于 hashcode 的节点

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }

        private static byte[] md5(String key) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md5.update(bytes);
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md5.digest();
        }
    }
}
