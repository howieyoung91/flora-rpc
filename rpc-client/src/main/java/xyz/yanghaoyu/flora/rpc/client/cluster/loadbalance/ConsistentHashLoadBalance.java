/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.base.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.base.cluster.URL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance extends AbstractServiceLoadBalance {
    public static final String NAME = "CONSISTENT_HASH";

    private final Map<String, ConsistentHashCircle> circles = new ConcurrentHashMap<>();
    private       Object[]                          NULL    = new Object[0];

    @Override
    protected URL doSelect(List<URL> serverAddresses, Invocation invocation) {
        String serviceName = invocation.getServiceName();

        ConsistentHashCircle hashCircle = circles.get(serviceName);
        int                  hashcode   = System.identityHashCode(serverAddresses);
        if (hashCircle == null || !hashCircle.valid(hashcode)) {
            circles.put(serviceName, new ConsistentHashCircle(serverAddresses, 10, hashcode));
            // 这里要先 put 再 get, 最大限度保证数据一致
            hashCircle = circles.get(serviceName);
        }

        String key = buildKey(serviceName, invocation.getArguments());
        return hashCircle.select(key);
    }

    private String buildKey(String serviceName, Object[] args) {
        return serviceName + Arrays.hashCode(Arrays.stream(args == null ? NULL : args).toArray());
    }

    private static class ConsistentHashCircle {
        private final TreeMap<Long, URL> virtualInvokers = new TreeMap<>();
        private final long               hashcode;

        private ConsistentHashCircle(List<URL> urls, int replicaNumber, int hashCode) {
            this.hashcode = hashCode;
            mapUrls(urls, replicaNumber);
        }

        /**
         * 把每个 Provider 的 url 映射到哈希环上
         */
        private void mapUrls(List<URL> urls, int replicaNumber) {
            for (URL url : urls) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(url.getAddress() + i);
                    for (int j = 0; j < 4; j++) {
                        long h = hash(digest, j);
                        // todo remove url
                        virtualInvokers.put(h, url);
                    }
                }
            }
        }

        private URL select(String key) {
            long hash = hash(md5(key), 0);
            return selectForKey(hash);
        }

        private boolean valid(long hashcode) {
            return hashcode == this.hashcode;
        }

        private static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        private URL selectForKey(long hashCode) {
            Map.Entry<Long, URL> entry =
                    virtualInvokers.tailMap(hashCode, true).firstEntry();

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
