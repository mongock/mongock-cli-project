package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.Jar;
import io.mongock.cli.wrapper.jars.JarFactory;
import io.mongock.cli.wrapper.jars.JarUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.mongock.cli.wrapper.argument.Argument.DRIVER;
import static io.mongock.cli.wrapper.argument.Argument.USER_APP_JAR;


public interface LauncherCliJar {


    LauncherCliJar loadClasses();

    void launch(String[] args);


    static LauncherBuilder builder() {
        return new LauncherBuilder();
    }

    class LauncherBuilder {

        private JarFactory jarFactory;

        private Jar userJar;
        private DriverWrapper driverWrapper;
        private String licenseKey;


        public LauncherBuilder() {
        }

        public LauncherBuilder setJarFactory(JarFactory jarFactory) {
            this.jarFactory = jarFactory;
            return this;
        }

        /**
         * Optional setter if Mongock is run based on an application
         *
         * @param userJar application jar
         * @return builder
         */
        public LauncherBuilder setUserJar(Jar userJar) {
            this.userJar = userJar;
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

        public LauncherBuilder setLicenseKey(String licenseKey) {
            this.licenseKey = licenseKey;
            return this;
        }

        public LauncherCliJar build() {
            if (userJar != null) {
                if (JarUtil.isSpringApplication(userJar.getJarFileArchive())) {
                    return buildLauncherSpring(jarFactory.cliSpringboot());
                } else {
                    return buildLauncherStandalone(jarFactory.cliCore());
                }
            } else {
                return buildLauncherWithoutApp(jarFactory.cliCore());
            }
        }

        private LauncherDefault buildLauncherWithoutApp(Jar cliJar) {
            Jar appJar = licenseKey != null ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp();
            validateNotNullParameter(cliJar.getPath(), "library cli core jar ");
            validateDriverIfApply();
            return new LauncherDefault(
                    appJar,
                    cliJar,
                    licenseKey,
                    driverWrapper,
                    licenseKey != null ? jarFactory.runnerProfessionalDependencies() : jarFactory.runnerCommunityDependencies()
            );
        }


        private LauncherStandalone buildLauncherStandalone(Jar cliJar) {
            validateNotNullParameter(userJar.getPath(), "parameter " + USER_APP_JAR.getDefaultName());
            validateNotNullParameter(cliJar.getPath(), "library cli core jar ");
            return new LauncherStandalone(userJar, cliJar);
        }

        private LauncherSpringboot buildLauncherSpring(Jar cliJar) {
            validateNotNullParameter(userJar.getPath(), "parameter " + USER_APP_JAR.getDefaultName());
            validateNotNullParameter(cliJar.getPath(), "library cli spring jar ");
            return new LauncherSpringboot(userJar, cliJar);
        }


        private void validateNotNullParameter(Object parameter, String name) {
            if (parameter == null) {
                throw new RuntimeException(name + " must be provided");
            }
        }

        private void validateDriverIfApply() {
            if (driverWrapper == null) {
                String drivers = Arrays.stream(DriverWrapper.values())
                        .map(DriverWrapper::name)
                        .collect(Collectors.joining("\n"));
                String message = String.format("When application is missing, Parameter `%s` must be provided :  \n%s", DRIVER.getDefaultName(), drivers);
                throw new RuntimeException(message);
            }
        }
    }

}
