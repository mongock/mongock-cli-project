package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.Jar;
import io.mongock.cli.wrapper.jars.JarFactory;
import io.mongock.cli.wrapper.jars.JarUtil;

import java.util.Arrays;
import java.util.Optional;
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

//        private String cliSpringJar;
//        private String cliCoreJar;

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
            if (getAppJar().isPresent()) {
                if (JarUtil.isSpringApplication(userJar.getJarFileArchive())) {
                    return buildLauncherSpring(jarFactory.cliSpringboot());
                } else {
                    return buildLauncherStandalone(jarFactory.cliCore());
                }
            } else {
                return buildLauncherWithoutApp(jarFactory.cliCore());
            }
        }

        private LauncherDefault buildLauncherWithoutApp(String cliJar) {
            Jar appJar = licenseKey != null ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp();
            validateNotNullParameter(cliJar, "library cli core jar ");
            if (driverWrapper == null) {
                String drivers = Arrays.stream(DriverWrapper.values())
                        .map(DriverWrapper::name)
                        .collect(Collectors.joining("\n"));
                String message = String.format("When application is missing, Parameter `%s` must be provided :  \n%s", DRIVER.getDefaultName(), drivers);
                throw new RuntimeException(message);
            }
            return new LauncherDefault(
                    appJar.getJarFileArchive(),
                    appJar.getUrl(),
                    cliJar,
                    licenseKey,
                    driverWrapper,
                    licenseKey != null ? jarFactory.runnerProfessionalDependencies() : jarFactory.runnerCommunityDependencies()
            );
        }

        private LauncherStandalone buildLauncherStandalone(String cliJar) {
            validateNotNullParameter(userJar.getUrl(), "parameter " + USER_APP_JAR.getDefaultName());
            validateNotNullParameter(cliJar, "library cli core jar ");
            return new LauncherStandalone(userJar.getJarFileArchive(), userJar.getUrl(), cliJar);
        }

        private LauncherSpringboot buildLauncherSpring(String cliJar) {
            validateNotNullParameter(userJar.getUrl(), "parameter " + USER_APP_JAR.getDefaultName());
            validateNotNullParameter(cliJar, "library cli spring jar ");
            return new LauncherSpringboot(userJar.getJarFileArchive(), userJar.getUrl(), cliJar);
        }

        private Optional<String> getAppJar() {
            return Optional.ofNullable(userJar.getUrl());
        }

        private void validateNotNullParameter(Object parameter, String name) {
            if (parameter == null) {
                throw new RuntimeException(name + " must be provided");
            }
        }
    }

}
