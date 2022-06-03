package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.CliClassLoader;
import io.mongock.cli.wrapper.jars.Jar;
import io.mongock.cli.wrapper.jars.JarFactory;

import java.util.Arrays;
import java.util.stream.Collectors;

import static io.mongock.cli.wrapper.argument.Argument.DRIVER;
import static io.mongock.cli.wrapper.argument.Argument.USER_APP_JAR;


public interface LauncherCliJar {


    void launch(String[] args);


    static LauncherBuilder builder(JarFactory jarFactory) {
        return new LauncherBuilder(jarFactory);
    }

    class LauncherBuilder {

        private JarFactory jarFactory;

        private Jar userJar;
        private DriverWrapper driverWrapper;
        private String licenseKey;


        public LauncherBuilder(JarFactory jarFactory) {
            this.jarFactory = jarFactory;
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
                if (userJar.isSpringApplication()) {
                    new CliClassLoader().addJar(userJar).loadClasses();
                    return new LauncherSpringboot(userJar, jarFactory.cliSpringboot());
                } else {

                    CliClassLoader cliClassLoader = new CliClassLoader()
                            .addJar(userJar)
                            .addJar(jarFactory.cliCore());
                    cliClassLoader.loadClasses();
                    return new LauncherStandalone(userJar, cliClassLoader.getClassLoader());
                }
            } else {

                CliClassLoader cliClassLoader = new CliClassLoader()
                        .addJar(isProfessional() ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp())
                        .addJar(jarFactory.cliCore())
                        .addJar(new Jar(driverWrapper.getJarPath()))
                        .addJars(isProfessional() ? jarFactory.runnerProfessionalDependencies() : jarFactory.runnerCommunityDependencies());
                cliClassLoader.loadClasses();
                validateDriverIfApply();
                return new LauncherDefault(
                        isProfessional() ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp(),
                        licenseKey,
                        driverWrapper,
                        cliClassLoader.getClassLoader()
                );

            }
        }

        private boolean isProfessional() {
            return licenseKey != null;
        }

        private void validateDriverIfApply() {
            if (driverWrapper == null) {
                throw new RuntimeException(String.format(
                        "When application is missing, Parameter `%s` must be provided :  \n%s",
                        DRIVER.getDefaultName(),
                        DriverWrapper.getAllDriverNames("\n")));
            }
        }
    }

}
