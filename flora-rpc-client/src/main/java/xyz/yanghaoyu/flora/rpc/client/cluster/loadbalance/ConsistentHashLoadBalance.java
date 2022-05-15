/*
 * Copyright ©2022-2022 Howie Young, All rights reserved.
 * Copyright ©2022-2022 杨浩宇，保留所有权利。
 */

package xyz.yanghaoyu.flora.rpc.client.cluster.loadbalance;

import xyz.yanghaoyu.flora.rpc.client.cluster.Invocation;
import xyz.yanghaoyu.flora.rpc.client.cluster.URL;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

        String key = serviceName +
                     Arrays.hashCode(Arrays.stream(
                             invocation.getArguments() == null
                                     ? NULL
                                     : invocation.getArguments()
                     ).toArray());
        return hashCircle.select(key);
    }

    private static class ConsistentHashCircle {
        private final TreeMap<Long, URL> virtualInvokers;
        private final long               hashcode;

        ConsistentHashCircle(List<URL> urls, int replicaNumber, int hashCode) {
            this.virtualInvokers = new TreeMap<>();
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
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, url);
                    }
                }
            }
        }

        public URL select(String key) {
            System.out.println(key);
            long hash = hash(md5(key), 0);
            return selectForKey(hash);
        }

        public boolean valid(long hashcode) {
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

        static byte[] md5(String key) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md5.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return md5.digest();
        }
    }
}
