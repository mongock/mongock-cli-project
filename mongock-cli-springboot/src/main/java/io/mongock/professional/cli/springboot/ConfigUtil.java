package io.mongock.professional.cli.springboot;

import io.mongock.professional.cli.springboot.config.CliProperties;
import io.mongock.professional.cli.springboot.config.WrapperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

final class ConfigUtil {

    private final static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    private ConfigUtil() {
    }

    static CliProperties getMongockCliProperties() {
        WrapperProperties wrapper = new Yaml().loadAs(MongockSpringbootCli.class.getClassLoader().getResourceAsStream("mongock-cli.yaml"), WrapperProperties.class);
        CliProperties cliProperties = wrapper.getMongock().getCli();
        cliProperties.getConfigSources().forEach(source ->  logger.debug("Added source class " + source));
        return cliProperties;
    }

    static Class<?>[] getConfigSourcesFromProperties(CliProperties cliProperties) {
        Function<String, Class<?>> getClassFromName = className -> {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
        List<Class<?>> userConfigSources = cliProperties.getConfigSources()
                .stream()
                .map(getClassFromName)
                .collect(Collectors.toList());
        Class<?>[] sources = new Class[userConfigSources.size() + 1];
        sources[0] = MongockSpringbootCli.class;
        for (int i = 0; i < userConfigSources.size(); i++) {
            sources[i + 1] = userConfigSources.get(i);
        }

        for (Class<?> source : sources) {
            logger.debug("Added source class " + source.getSimpleName());
        }
        return sources;
    }
}