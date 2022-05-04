package xyz.yanghaoyu.flora.rpc.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public final class ZooKeeper {
    // todo 目前这些代码都是写死的 以后需要从外部读入配置
    private static final    int              BASE_SLEEP_TIME   = 1000;
    private static final    int              MAX_RETRIES       = 3;
    private static final    String           ZOOKEEPER_ADDRESS = "localhost:12345";
    private static final    String           NAMESPACE         = "flora";
    private static final    RetryPolicy      RETRY_POLICY      =
            new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
    private static volatile CuratorFramework ZOOKEEPER_CLIENT;


    public static CuratorFramework client() {
        // double check
        if (ZOOKEEPER_CLIENT != null) {
            synchronized (ZooKeeper.class) {
                if (ZOOKEEPER_CLIENT != null) {
                    return ZOOKEEPER_CLIENT;
                }
            }
        }

        return newCuratorClient();
    }

    private static CuratorFramework newCuratorClient() {
        ZOOKEEPER_CLIENT = CuratorFrameworkFactory.builder()
                .connectString(ZOOKEEPER_ADDRESS)
                .retryPolicy(RETRY_POLICY)
                .build();
        ZOOKEEPER_CLIENT.start();
        try {
            if (!ZOOKEEPER_CLIENT.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to zookeeper");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ZOOKEEPER_CLIENT;
    }
}
