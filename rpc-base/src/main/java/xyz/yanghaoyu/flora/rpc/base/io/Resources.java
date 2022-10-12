package xyz.yanghaoyu.flora.rpc.base.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Resources {
    public static Reader getResourceAsReader(String filename) throws IOException {
        return new InputStreamReader(getResourceAsStream(filename));
    }

    public static InputStream getResourceAsStream(String filename) throws IOException {
        for (ClassLoader classLoader : getClassLoaders()) {
            InputStream inputStream = classLoader.getResourceAsStream(filename);
            if (null != inputStream) {
                return inputStream;
            }
        }
        throw new IOException("Could not find resource " + filename);
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{
                ClassLoader.getSystemClassLoader(),
                Thread.currentThread().getContextClassLoader()
        };
    }

}
