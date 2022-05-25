package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.util.JarFactory;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.mongock.cli.wrapper.util.Arguments.APP_JAR;
import static io.mongock.cli.wrapper.util.Arguments.DRIVER;


public interface LauncherCliJar {


    LauncherCliJar loadClasses();

    void launch(String[] args);


    static LauncherBuilder builder() {
        return new LauncherBuilder();
    }

    class LauncherBuilder {

        private JarFactory jarFactory;

        private String cliSpringJar;
        private String cliCoreJar;

        private String appJarFile;
        private DriverWrapper driverWrapper;


        public LauncherBuilder() {
        }

        public LauncherBuilder setJarFactory(JarFactory jarFactory) {
            this.jarFactory = jarFactory;
            return this;
        }


        /**
         * Optional setter if Mongock is run based on an application
         *
         * @param appJarFile application jar
         * @return builder
         */
        public LauncherBuilder setAppJarFile(String appJarFile) {
            this.appJarFile = appJarFile;
            return this;
        }


        /**
         * Required if the application is not provided
         *
         * @param driverWrapper the driver wrapper enum
         * @return builder
         */
        public LauncherBuilder setDriverWrapper(DriverWrapper driverWrapper) {
            this.driverWrapper = driverWrapper;
            return this;
        }


        public LauncherCliJar build() throws IOException {
            cliCoreJar = jarFactory.cliCore();
            cliSpringJar = jarFactory.cliSpringboot();
            if (getAppJar().isPresent()) {
                JarFileArchive appArchive = new JarFileArchive(new File(appJarFile));
                if (JarUtil.isSpringApplication(appArchive)) {
                    return buildLauncherSpring(appArchive, appJarFile);
                } else {
                    return buildLauncherStandalone(appArchive, appJarFile);
                }
            } else {
                String defaultAppJarFile = jarFactory.defaultApp();
                JarFileArchive defaultAppArchive = new JarFileArchive(new File(defaultAppJarFile));
                return buildLauncherWithoutApp(defaultAppArchive, defaultAppJarFile);
            }
        }

        private LauncherDefault buildLauncherWithoutApp(JarFileArchive appArchive, String appJar) {
            validateNotNullParameter(cliCoreJar, "library cli core jar ");
            if (driverWrapper == null) {
                String drivers = Arrays.stream(DriverWrapper.values())
                        .map(DriverWrapper::name)
                        .collect(Collectors.joining("\n"));
                String message = String.format("When application is missing, Parameter `%s` must be provided :  \n%s", DRIVER.getDefaultName(), drivers);
                throw new RuntimeException(message);
            }
            return new LauncherDefault(
                    appArchive,
                    appJar,
                    cliCoreJar,
                    jarFactory.runnerStandaloneBase(),
                    jarFactory.runnerStandalone(),
                    driverWrapper
            );
        }

        private LauncherStandalone buildLauncherStandalone(JarFileArchive appArchive, String appJar) {
            validateNotNullParameter(appJar, "parameter " + APP_JAR.getDefaultName());
            validateNotNullParameter(cliCoreJar, "library cli core jar ");
            return new LauncherStandalone(appArchive, appJar, cliCoreJar);
        }

        private LauncherSpringboot buildLauncherSpring(JarFileArchive appArchive, String appJar) {
            validateNotNullParameter(appJar, "parameter " + APP_JAR.getDefaultName());
            validateNotNullParameter(cliSpringJar, "library cli spring jar ");
            return new LauncherSpringboot(appArchive, appJar, cliSpringJar);
        }


        private Optional<String> getAppJar() {
            return Optional.ofNullable(appJarFile);
        }

        private void validateNotNullParameter(Object parameter, String name) {
            if (parameter == null) {
                throw new RuntimeException(name + " must be provided");
            }
        }
    }

}
