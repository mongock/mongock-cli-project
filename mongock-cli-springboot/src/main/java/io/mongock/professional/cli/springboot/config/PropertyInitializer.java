package io.mongock.professional.cli.springboot.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class PropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        Map<String, Object> myProperties = new HashMap<>();
        myProperties.put("mongock-mode-cli", true);
        environment.getPropertySources().addFirst(new MapPropertySource("mongock-properties", myProperties));
    }
}
