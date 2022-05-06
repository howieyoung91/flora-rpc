package xyz.yanghaoyu.flora.rpc.config;

import xyz.yanghaoyu.flora.rpc.io.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * todo
 */
public class Configuration {
    Properties props = new Properties();

    public Configuration(InputStream inputStream) {
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("fail to load configuration");
        }
    }

    public Object get(String key) {
        return props.get(key);
    }

    public Integer serverPort() {
        return (Integer) props.get("flora.rpc.server.port");
    }

    public static Configuration load(String location) {
        InputStream is = null;
        try {
            is = Resources.getResourceAsStream(location);
            return new Configuration(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("fail to load configuration: " + location);
        }
    }
}
