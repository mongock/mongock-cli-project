package io.mongock.cli.wrapper;

import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.launcher.LauncherCliJar;
import io.mongock.cli.wrapper.util.ArgsUtil;

import java.util.stream.Stream;

import static io.mongock.cli.util.logger.CliLogger.Level.INFO;
import static io.mongock.cli.wrapper.util.ArgsUtil.getCleanArgs;
import static io.mongock.cli.wrapper.util.Parameters.APP_JAR_ARG_LONG;
import static io.mongock.cli.wrapper.util.Parameters.APP_JAR_ARG_SHORT;
import static io.mongock.cli.wrapper.util.Parameters.CLI_CORE_JAR_ARG;
import static io.mongock.cli.wrapper.util.Parameters.CLI_SPRING_JAR_ARG;
import static io.mongock.cli.wrapper.util.Parameters.LOG_LEVEL_ARG;
import static io.mongock.cli.wrapper.util.Parameters.MONGOCK_CORE_JAR_ARG;

public class MongockCli {
    private static final CliLogger logger = CliLoggerFactory.getLogger(MongockCli.class);

    private static final String[] argumentsToCleanUp = {
            APP_JAR_ARG_LONG,
            APP_JAR_ARG_SHORT,
            CLI_SPRING_JAR_ARG,
            CLI_CORE_JAR_ARG,
            LOG_LEVEL_ARG,
            MONGOCK_CORE_JAR_ARG
    };

    static {
        Banner.print(System.out);
    }

    public static void main(String... args) {
        setLogger(args);
        printArgs(args);

        try {

            String appJar = ArgsUtil.getOptionalParam(args, APP_JAR_ARG_LONG)
                    .orElseGet(() -> ArgsUtil.getParameter(args, APP_JAR_ARG_SHORT, false));

            LauncherCliJar.builder()
                    .setAppJarFile(appJar)
                    .setCliCoreJar(ArgsUtil.getParameter(args, CLI_CORE_JAR_ARG, false))
                    .setCliSpringJar(ArgsUtil.getParameter(args, CLI_SPRING_JAR_ARG, false))
                    .setMongockCoreJarFile(ArgsUtil.getParameter(args, MONGOCK_CORE_JAR_ARG, false))
                    .build()
                    .loadClasses()
                    .launch(getCleanArgs(args, argumentsToCleanUp));
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage());
            System.exit(1);
        }

    }

    // Unneeded loop when level > DEBUG...but small ;)
    private static void printArgs(String[] args) {
        StringBuilder sb = new StringBuilder("CLI arguments: ");
        Stream.of(args).forEach(arg -> sb.append(arg).append(" "));
        logger.debug(sb.toString());
    }

    private static void setLogger(String[] args) {
        CliLoggerFactory.setLevel(ArgsUtil.getOptionalParam(args, LOG_LEVEL_ARG)
                .map(CliLogger.Level::fromStringDefaultInfo)
                .orElse(INFO));
    }

}
