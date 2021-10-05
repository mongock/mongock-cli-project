package io.mongock.professional.cli.springboot;

import io.mongock.professional.cli.core.CommandHelper;
import io.mongock.professional.cli.core.MongockCli;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderBase;
import io.mongock.utils.Constants;
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

import static io.mongock.runner.core.builder.BuilderType.PROFESSIONAL;
import static picocli.CommandLine.IFactory;

@SpringBootApplication
class MongockSpringbootCommandLine implements CommandLineRunner, ExitCodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MongockSpringbootCommandLine.class);

    private static Class<?>[] sources;

    private int exitCode;

    @Autowired
    private RunnerBuilder builder;

    @Autowired(required = false)
    private IFactory factory;


    //This is called from the Springboot launcher in the wrapper project
    public static void setSources(Class<?>... sources) {
        MongockSpringbootCommandLine.sources = new Class[sources.length + 1];
        MongockSpringbootCommandLine.sources[0] = MongockSpringbootCommandLine.class;
        for(int i=0 ; i< sources.length ; i++) {
            Class<?> source = sources[i];
            logger.debug("Setting config source: {}", source);
            MongockSpringbootCommandLine.sources[i + 1] = source;
        }
    }

    public static Class<?>[] getSources() {
        if(sources != null) {
            return sources;
        } else  {
            logger.warn("Sources not added. This will probably cause the process to break");
            return new Class<?>[]{MongockSpringbootCommandLine.class};
        }
    }

    public static void main(String... args) {
        SpringApplicationBuilder springApplicationBuilder =  new SpringApplicationBuilder()
                .web(WebApplicationType.NONE)
                .banner(new MongockBanner())
                .logStartupInfo(false)
                .sources(getSources())
                .profiles(Constants.CLI_PROFILE);
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

        return ((RunnerBuilderBase)builder).getType() == PROFESSIONAL;
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

}
