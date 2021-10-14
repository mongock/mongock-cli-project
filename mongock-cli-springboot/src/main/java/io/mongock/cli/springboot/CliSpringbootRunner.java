package io.mongock.cli.springboot;

import io.mongock.cli.core.CommandHelper;
import io.mongock.cli.core.CliCoreRunner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import picocli.CommandLine;

import java.util.Properties;

import static io.mongock.runner.core.builder.BuilderType.PROFESSIONAL;
import static picocli.CommandLine.IFactory;

@SpringBootApplication
class CliSpringbootRunner implements org.springframework.boot.CommandLineRunner, ExitCodeGenerator {

    private static final CliLogger logger = CliLoggerFactory.getLogger(CliSpringbootRunner.class);

    private static Class<?>[] sources;

    private int exitCode;

    @Autowired
    private RunnerBuilder builder;

    @Autowired(required = false)
    private IFactory factory;


    //This is called from the Springboot launcher in the wrapper project
    public static void setSources(Class<?>... sources) {
        CliSpringbootRunner.sources = new Class[sources.length + 1];
        CliSpringbootRunner.sources[0] = CliSpringbootRunner.class;
        for(int i=0 ; i< sources.length ; i++) {
            Class<?> source = sources[i];
            logger.debug("Setting config source: {}", source);
            CliSpringbootRunner.sources[i + 1] = source;
        }
    }

    public static Class<?>[] getSources() {
        if(sources != null) {
            return sources;
        } else  {
            logger.warn("Sources not added. This will probably cause the process to break");
            return new Class<?>[]{CliSpringbootRunner.class};
        }
    }

    public static void main(String... args) {
        Properties properties = getProperties();

        SpringApplicationBuilder springApplicationBuilder =  new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .banner(new MongockBanner())
                .logStartupInfo(false)
                .sources(getSources())
                .properties(properties)
                .profiles(Constants.CLI_PROFILE);
        System.exit(SpringApplication.exit(springApplicationBuilder.run(args)));
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty("logging.level.root", "ERROR");
        properties.setProperty("logging.level.io.mongock", "INFO");
        return properties;
    }


    @Override
    public void run(String... args) {
        if(args.length == 0) {
            logger.error("command format: 'mongock [operation] [parameters]'");
            exitCode = CommandLine.ExitCode.USAGE;
        } else {
            if(CommandHelper.isProfessionalCommand(args[0]) && builder.getType() != PROFESSIONAL){
                logger.error("Professional operation {} not supported in community edition", args[0]);
                exitCode = CommandLine.ExitCode.USAGE;
            } else {
                exitCode = CliCoreRunner
                        .builder()
                        .factory(factory)
                        .runnerBuilder(builder)
                        .build()
                        .execute(args);
            }
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

}
