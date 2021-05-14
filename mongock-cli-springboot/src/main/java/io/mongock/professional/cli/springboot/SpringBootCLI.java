package io.mongock.professional.cli.springboot;

import io.mongock.professional.cli.springboot.config.MongockBanner;
import io.mongock.professional.cli.springboot.config.MongockCliProperties;
import io.mongock.professional.cli.springboot.config.PropertyInitializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.List;

import static picocli.CommandLine.IFactory;


//TODO support .yaml and .properties
//TODO support profiles

@SpringBootApplication
class SpringBootCLI extends CliRunner {


    @Value("${myProperty:DEFAULT_VALUE}")
    private String myProperty;

    public SpringBootCLI(IFactory factory) {
        super(factory);
    }

    public static void main(String... args) {
        System.exit(SpringApplication.exit(builder().run(args)));
    }

    private static SpringApplicationBuilder builder() {
        MongockCliProperties cliProperties = getMongockCliProperties();
        return new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .initializers(new PropertyInitializer())
                .banner(new MongockBanner())
                .logStartupInfo(false)
                .sources(getConfigSources(cliProperties))
                .profiles("mongock");
    }

    private static Class<?>[] getConfigSources(MongockCliProperties cliProperties) {
        List<Class<?>> userConfigSources = cliProperties.getConfigSources();
        Class<?>[] sources = new Class[userConfigSources.size() + 1];
        sources[0] = SpringBootCLI.class;
        for(int i=0;i<userConfigSources.size();i++) {
            Class<?> aClass = userConfigSources.get(i);
            System.out.println("Added source class " + aClass.getSimpleName());
            sources[i] = aClass;
        }
        return sources;
    }

    private static MongockCliProperties getMongockCliProperties() {
        return new MongockCliProperties(new ArrayList<>());
//        return new Yaml().loadAs(SpringBootCLI.class.getClassLoader().getResourceAsStream("mongock-cli.yaml"), MongockCliProperties.class);
    }

}