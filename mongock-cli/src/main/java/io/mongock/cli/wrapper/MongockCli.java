package io.mongock.cli.wrapper;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.jars.Jar;
import io.mongock.cli.wrapper.launcher.LauncherCliJar;
import io.mongock.cli.wrapper.argument.Argument;
import io.mongock.cli.wrapper.argument.ArgumentsHolder;
import io.mongock.cli.wrapper.jars.JarFactory;

import java.util.stream.Stream;

import static io.mongock.cli.util.logger.CliLogger.Level.INFO;
import static io.mongock.cli.wrapper.argument.Argument.USER_APP_JAR;
import static io.mongock.cli.wrapper.argument.Argument.CLI_VERSION;
import static io.mongock.cli.wrapper.argument.Argument.COMMUNITY_VERSION;
import static io.mongock.cli.wrapper.argument.Argument.DRIVER;
import static io.mongock.cli.wrapper.argument.Argument.LICENSE_KEY;
import static io.mongock.cli.wrapper.argument.Argument.LOG_LEVEL;
import static io.mongock.cli.wrapper.argument.Argument.PROFESSIONAL_VERSION;

public class MongockCli {

    private static final String JARS_LIB = "lib";

    private static ArgumentsHolder argumentsHolder;
    private static final CliLogger logger = CliLoggerFactory.getLogger(MongockCli.class);

    static {
        Banner.print(System.out);
    }

    public static void main(String... args) {
        Argument.validateArguments();
        argumentsHolder = new ArgumentsHolder(args);
        setLogger();
        printArgs(args);

        try {
            LauncherCliJar.builder()
                    .setUserJar(new Jar(argumentsHolder.getOrNull(USER_APP_JAR)))
                    .setJarFactory(buildJarFactory())
                    .setDriverWrapper(getDriverWrapper())
                    .setLicenseKey(argumentsHolder.getOrNull(LICENSE_KEY))
                    .build()
                    .loadClasses()
                    .launch(argumentsHolder.getCleanArgs());
            System.exit(0);
        } catch (Exception ex) {
            logger.error(ex);
            System.exit(1);
        }

    }

    private static DriverWrapper getDriverWrapper() {
        String driverName = argumentsHolder.getOrNull(DRIVER);
        if (driverName != null) {
            return DriverWrapper.getDriver(driverName)
                    .setJarsLibFolder(JARS_LIB)
                    .setVersion(argumentsHolder.getOrException(CLI_VERSION));
        } else {
            return null;
        }


    }

    private static JarFactory buildJarFactory() {
        return new JarFactory(
                JARS_LIB,
                argumentsHolder.getOrException(CLI_VERSION),
                argumentsHolder.getOrException(COMMUNITY_VERSION),
                argumentsHolder.getOrException(PROFESSIONAL_VERSION));
    }


    // Unneeded loop when level > DEBUG...but small ;)
    private static void printArgs(String[] args) {
        StringBuilder sb = new StringBuilder("CLI arguments: ");
        Stream.of(args).forEach(arg -> sb.append(arg).append(" "));
        logger.debug(sb.toString());
    }

    private static void setLogger() {
        CliLoggerFactory.setLevel(argumentsHolder.getOptional(LOG_LEVEL)
                .map(CliLogger.Level::fromStringDefaultInfo)
                .orElse(INFO));
    }

}
