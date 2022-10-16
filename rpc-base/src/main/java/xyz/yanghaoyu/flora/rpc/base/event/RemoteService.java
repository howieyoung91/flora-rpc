package xyz.yanghaoyu.flora.rpc.base.event;

import xyz.yanghaoyu.flora.rpc.base.cluster.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Howie Young
 * @version 1.0
 * @since 1.0 [2022/10/15 20:47]
 */
public class RemoteService {
    String          name; // service name
    Collection<URL> urls = new ArrayList<>();

    public RemoteService(String name) {
        this.name = name;
    }

    public RemoteService(String name, Collection<URL> urls) {
        this.name = name;
        this.urls = urls;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<URL> getUrls() {
        return urls;
    }

    public void setUrls(Collection<URL> urls) {
        this.urls = urls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RemoteService that = (RemoteService) o;
        return Objects.equals(name, that.name) && Objects.equals(urls, that.urls);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, urls);
    }
}
