package io.mongock.professional.cli.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("mongock.cli")
public class MongockCliProperties {

    public MongockCliProperties() {
    }

    public MongockCliProperties(List<Class<?>> configSources) {
        this.configSources = configSources;
    }

    private List<Class<?>> configSources;

    public List<Class<?>> getConfigSources() {
        return configSources;
    }


    public void setConfigSources(List<Class<?>> configSources) {
        this.configSources = configSources;
    }
}
