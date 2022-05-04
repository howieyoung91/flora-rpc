package xyz.yanghaoyu.flora.rpc.config;

public abstract class ConfigurationBuilder {
    protected final Configuration configuration;

    public ConfigurationBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
