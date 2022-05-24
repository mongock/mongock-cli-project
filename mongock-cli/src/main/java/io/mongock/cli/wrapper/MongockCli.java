package io.mongock.cli.wrapper;

import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.launcher.LauncherCliJar;
import io.mongock.cli.wrapper.util.ArgsUtil;
import io.mongock.cli.wrapper.util.DriverWrapperFactory;
import io.mongock.cli.wrapper.util.JarFactory;

import java.util.stream.Stream;

import static io.mongock.cli.util.logger.CliLogger.Level.INFO;
import static io.mongock.cli.wrapper.util.ArgsUtil.getCleanArgs;
import static io.mongock.cli.wrapper.util.Arguments.APP_JAR;
import static io.mongock.cli.wrapper.util.Arguments.CLI_VERSION;
import static io.mongock.cli.wrapper.util.Arguments.COMMUNITY_VERSION;
import static io.mongock.cli.wrapper.util.Arguments.DRIVER;
import static io.mongock.cli.wrapper.util.Arguments.LOG_LEVEL;
import static io.mongock.cli.wrapper.util.Arguments.PROFESSIONAL_VERSION;

public class MongockCli {

    private static final String JARS_LIB = "lib";
    private static final CliLogger logger = CliLoggerFactory.getLogger(MongockCli.class);

    static {
        Banner.print(System.out);
    }

    public static void main(String... args) {
        setLogger(args);
        printArgs(args);

        try {
            LauncherCliJar.builder()
                    .setAppJarFile(getAppJar(args))
                    .setJarFactory(buildJarFactory(args))
                    .setDriverJar(getDriver(args))
                    .build()
                    .loadClasses()
                    .launch(getCleanArgs(args));
            System.exit(0);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            System.exit(1);
        }

    }

    private static String getDriver(String[] args) {
        String driverParameter = ArgsUtil.getOptionalParam(args, DRIVER.getLongName())
                .orElseGet(() -> ArgsUtil.getParameter(args, DRIVER.getShortName(), false));
        if (driverParameter != null) {
            String cliVersion = ArgsUtil.getParameter(args, CLI_VERSION.getDefaultName(), true);
            DriverWrapperFactory driverWrapperFactory = new DriverWrapperFactory(JARS_LIB, cliVersion);
            return driverWrapperFactory.getJar(driverParameter);
        } else {
            return null;
        }
    }

    private static JarFactory buildJarFactory(String[] args) {
        return new JarFactory(
                JARS_LIB,
                ArgsUtil.getParameter(args, CLI_VERSION.getDefaultName(), true),
                ArgsUtil.getParameter(args, COMMUNITY_VERSION.getDefaultName(), true),
                ArgsUtil.getParameter(args, PROFESSIONAL_VERSION.getDefaultName(), true));
    }

    private static String getAppJar(String[] args) {
        return ArgsUtil.getOptionalParam(args, APP_JAR.getLongName())
                .orElseGet(() -> ArgsUtil.getParameter(args, APP_JAR.getShortName(), false));
    }

    // Unneeded loop when level > DEBUG...but small ;)
    private static void printArgs(String[] args) {
        StringBuilder sb = new StringBuilder("CLI arguments: ");
        Stream.of(args).forEach(arg -> sb.append(arg).append(" "));
        logger.debug(sb.toString());
    }

    private static void setLogger(String[] args) {
        CliLoggerFactory.setLevel(ArgsUtil.getOptionalParam(args, LOG_LEVEL.getDefaultName())
                .map(CliLogger.Level::fromStringDefaultInfo)
                .orElse(INFO));
    }

}
