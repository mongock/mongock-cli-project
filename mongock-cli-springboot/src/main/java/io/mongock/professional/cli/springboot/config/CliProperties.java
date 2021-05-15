package io.mongock.professional.cli.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("mongock.cli")
public class CliProperties {

    private List<String> configSources;

    public List<String> getConfigSources() {
        return configSources;
    }

    public CliProperties() {
    }

    public CliProperties(List<String> configSources) {
        this.configSources = configSources;
    }


    public void setConfigSources(List<String> configSources) {
        this.configSources = configSources;
    }

    @Override
    public String toString() {
        return "MongockCliProperties{" +
                "configSources=" + configSources +
                '}';
    }
}
