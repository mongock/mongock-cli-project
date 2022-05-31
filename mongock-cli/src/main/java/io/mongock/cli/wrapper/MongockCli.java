package io.mongock.cli.wrapper;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.launcher.LauncherCliJar;
import io.mongock.cli.wrapper.util.ArgsUtil;
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
    private static final String licenseKey = "eyJhbGciOiJSUzI1NiJ9.eyJ0b2tlblZlcnNpb24iOiIxLjAiLCJqdGkiOiJsay1jZjA0YjkxNC1lYTUxLTRiMDUtYmFjNS00MTFiYmZiZjk3NGQiLCJhdWQiOiJjLW9zYW50YW5hQG1vbmdvY2suaW8iLCJpc3MiOiJtb25nb2NrLXNlcnZlciIsInN1YiI6InRyaWFsLWxpY2Vuc2Uta2V5IiwiaWF0IjoxNjUwMzIzODM2LCJleHAiOjE2NjU4NzU4MzYsImxpY2Vuc2VJZCI6ImwtYzIyNzQwNTMtYzQ2My00NDM2LTgzMzAtNzA3ZTcxY2ZmYjY3IiwibGljZW5zZVR5cGUiOiJUUklBTCIsImVtYWlsIjoib3NhbnRhbmFAbW9uZ29jay5pbyIsImNvbXBhbnlOYW1lIjoiTW9uZ29jayBMdGQiLCJzY2hlbWFzIjoxfQ.mn00lxo4mskvRVDyECFubzlWj_iPzvqt8ARxWPPHYve5vEtgVJnG6cFaYD2oip0fDWpfIXfaqNNsscch4bw3pWgKons3j5tD6o-RQOmAbW_vQc-9AbaC3r6tAdwWl1aT80O-wAUE4rZLNHvWFZY4XVZNol-C_RGRsuC3eC5C1QnSv7ZiWcF4RNIpYqO1yTcto8AKHExz97fssLFkVihMtOYlLttntZmw7d2dOeS-36eSp-gc1dQ8iuwYLhhIp7n9KuCj9KVrd1wRB_H7LPuXU-EcfjQs6_x2KUdUKDOCa99h4X9sCe7Btm1pVJrSIDO6T0n1KaePuFIjwuEec7acyw";

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
                    .setDriverWrapper(getDriverWrapper(args))
                    .setLicenseKey(licenseKey)
                    .build()
                    .loadClasses()
                    .launch(getCleanArgs(args));
            System.exit(0);
        } catch (Exception ex) {
            logger.error(ex);
            System.exit(1);
        }

    }

    private static DriverWrapper getDriverWrapper(String[] args) {
        String driverName =  ArgsUtil.getOptionalParam(args, DRIVER.getLongName())
                .orElseGet(() -> ArgsUtil.getParameter(args, DRIVER.getShortName(), false));
        if(driverName != null ) {
            return DriverWrapper.getDriver(driverName)
                    .setJarsLibFolder(JARS_LIB)
                    .setVersion(ArgsUtil.getParameter(args, CLI_VERSION.getDefaultName(), true));
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
