package io.mongock.professional.cli.springboot;

import io.mongock.professional.cli.core.MongockCli;
import io.mongock.professional.cli.springboot.config.CliProperties;
import io.mongock.professional.cli.springboot.config.MongockBanner;
import io.mongock.professional.cli.springboot.config.PropertyInitializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static picocli.CommandLine.IFactory;


//TODO support .yaml and .properties
//TODO support profiles

@SpringBootApplication
class MongockSpringbootCli implements CommandLineRunner, ExitCodeGenerator {

    private final IFactory factory;

    private int exitCode;

    public MongockSpringbootCli(IFactory factory) {
        this.factory = factory;
    }

    public static void main(String... args) {
        CliProperties cliProperties = ConfigUtil.getMongockCliProperties();
        SpringApplicationBuilder springApplicationBuilder =  new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .initializers(new PropertyInitializer())
                .banner(new MongockBanner())
                .logStartupInfo(false)
                .sources(ConfigUtil.getConfigSourcesFromProperties(cliProperties))
                .profiles("mongock-cli");
        System.exit(SpringApplication.exit(springApplicationBuilder.run(args)));
    }


    @Override
    public void run(String... args) {
        exitCode = MongockCli
                .builder()
                .factory(factory)
                .allCommands()
                .build()
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }





}