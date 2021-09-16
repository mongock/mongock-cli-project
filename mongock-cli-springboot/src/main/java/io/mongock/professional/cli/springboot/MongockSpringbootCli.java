package io.mongock.professional.cli.springboot;

import io.mongock.professional.cli.core.CommandHelper;
import io.mongock.professional.cli.core.MongockCli;
import io.mongock.professional.cli.springboot.config.CliProperties;
import io.mongock.professional.cli.springboot.config.MongockBanner;
import io.mongock.professional.cli.springboot.config.PropertyInitializer;
import io.mongock.runner.core.builder.RunnerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import picocli.CommandLine;

import static picocli.CommandLine.IFactory;


//TODO support .yaml and .properties
//TODO support profiles

@SpringBootApplication
class MongockSpringbootCli implements CommandLineRunner, ExitCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MongockSpringbootCli.class);

    private final IFactory factory;

    private int exitCode;

    @Autowired
    private RunnerBuilder builder;

    @Autowired

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
        if(args.length == 0) {
            System.err.println("command format: 'mongock [operation] [parameters]'");
            exitCode = CommandLine.ExitCode.USAGE;
        } else if(CommandHelper.isProfessionalCommand(args[0]) && !isBuilderProfessional()){
            logger.error("Professional operation {} not supported in community edition", args[0]);
            exitCode = CommandLine.ExitCode.USAGE;
        } else {
            exitCode = MongockCli
                    .builder()
                    .factory(factory)
                    .allCommands()
                    .runnerBuilder(builder)
                    .build()
                    .execute(args);
        }
    }

    private boolean isBuilderProfessional() {
        //TODO improve this check
        return builder.getClass().getName().contains("professional");
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

}
