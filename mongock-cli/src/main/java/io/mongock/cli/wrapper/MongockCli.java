package io.mongock.cli.wrapper;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.launcher.LauncherCliJar;
import io.mongock.cli.wrapper.util.ArgumentsHolder;
import io.mongock.cli.wrapper.util.JarFactory;

import java.util.stream.Stream;

import static io.mongock.cli.util.logger.CliLogger.Level.INFO;
import static io.mongock.cli.wrapper.util.Argument.APP_JAR;
import static io.mongock.cli.wrapper.util.Argument.CLI_VERSION;
import static io.mongock.cli.wrapper.util.Argument.COMMUNITY_VERSION;
import static io.mongock.cli.wrapper.util.Argument.DRIVER;
import static io.mongock.cli.wrapper.util.Argument.LOG_LEVEL;
import static io.mongock.cli.wrapper.util.Argument.PROFESSIONAL_VERSION;

public class MongockCli {

    private static final String JARS_LIB = "lib";

    private static ArgumentsHolder argumentsHolder;
    private static final CliLogger logger = CliLoggerFactory.getLogger(MongockCli.class);
    private static final String licenseKey = "eyJhbGciOiJSUzI1NiJ9.eyJ0b2tlblZlcnNpb24iOiIxLjAiLCJqdGkiOiJsay1jZjA0YjkxNC1lYTUxLTRiMDUtYmFjNS00MTFiYmZiZjk3NGQiLCJhdWQiOiJjLW9zYW50YW5hQG1vbmdvY2suaW8iLCJpc3MiOiJtb25nb2NrLXNlcnZlciIsInN1YiI6InRyaWFsLWxpY2Vuc2Uta2V5IiwiaWF0IjoxNjUwMzIzODM2LCJleHAiOjE2NjU4NzU4MzYsImxpY2Vuc2VJZCI6ImwtYzIyNzQwNTMtYzQ2My00NDM2LTgzMzAtNzA3ZTcxY2ZmYjY3IiwibGljZW5zZVR5cGUiOiJUUklBTCIsImVtYWlsIjoib3NhbnRhbmFAbW9uZ29jay5pbyIsImNvbXBhbnlOYW1lIjoiTW9uZ29jayBMdGQiLCJzY2hlbWFzIjoxfQ.mn00lxo4mskvRVDyECFubzlWj_iPzvqt8ARxWPPHYve5vEtgVJnG6cFaYD2oip0fDWpfIXfaqNNsscch4bw3pWgKons3j5tD6o-RQOmAbW_vQc-9AbaC3r6tAdwWl1aT80O-wAUE4rZLNHvWFZY4XVZNol-C_RGRsuC3eC5C1QnSv7ZiWcF4RNIpYqO1yTcto8AKHExz97fssLFkVihMtOYlLttntZmw7d2dOeS-36eSp-gc1dQ8iuwYLhhIp7n9KuCj9KVrd1wRB_H7LPuXU-EcfjQs6_x2KUdUKDOCa99h4X9sCe7Btm1pVJrSIDO6T0n1KaePuFIjwuEec7acyw";

    static {
        Banner.print(System.out);
    }

    public static void main(String... args) {
        argumentsHolder = new ArgumentsHolder(args);
        setLogger();
        printArgs(args);

        try {
            LauncherCliJar.builder()
                    .setAppJarFile(argumentsHolder.getOrNull(APP_JAR))
                    .setJarFactory(buildJarFactory())
                    .setDriverWrapper(getDriverWrapper())
                    .setLicenseKey(licenseKey)
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
