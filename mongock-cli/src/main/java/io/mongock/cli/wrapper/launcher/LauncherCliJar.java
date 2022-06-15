package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.CliClassLoader;
import io.mongock.cli.wrapper.jars.Jar;
import io.mongock.cli.wrapper.jars.JarFactory;

import static io.mongock.cli.wrapper.argument.Argument.DRIVER;


public interface LauncherCliJar {


    void launch(String[] args);


    static LauncherBuilder builder(JarFactory jarFactory) {
        return new LauncherBuilder(jarFactory);
    }

    class LauncherBuilder {

        private JarFactory jarFactory;

        private Jar userApplicationJar;
        private Jar userChangeUnitJar;
        private DriverWrapper driverWrapper;
        private String licenseKey;


        public LauncherBuilder(JarFactory jarFactory) {
            this.jarFactory = jarFactory;
        }


        /**
         * Optional setter if Mongock is run based on an application
         *
         * @param userApplicationJar application jar
         * @return builder
         */
        public LauncherBuilder setUserApplicationJar(Jar userApplicationJar) {
            this.userApplicationJar = userApplicationJar;
            return this;
        }

        public LauncherBuilder setUserChangeUnitJar(Jar userChangeUnitJar) {
            this.userChangeUnitJar = userChangeUnitJar;
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
            if (userApplicationJar != null) {
                if (userApplicationJar.isSpringApplication()) {
                    new CliClassLoader().addJar(userApplicationJar).loadClasses();
                    return new LauncherSpringboot(userApplicationJar, jarFactory.cliSpringboot());
                } else {

                    CliClassLoader cliClassLoader = new CliClassLoader()
                            .addJar(userApplicationJar)
                            .addJar(jarFactory.cliCore());
                    cliClassLoader.loadClasses();
                    return new LauncherStandalone(userApplicationJar, cliClassLoader.getClassLoader());
                }
            } else if(userChangeUnitJar != null)  {
                CliClassLoader cliClassLoader = new CliClassLoader()
                        .addJar(isProfessional() ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp())
                        .addJar(jarFactory.cliCore())
                        .addJar(new Jar(driverWrapper.getJarPath()))
                        .addJars(isProfessional() ? jarFactory.runnerProfessionalDependencies() : jarFactory.runnerCommunityDependencies())
                        .addJar(userChangeUnitJar);
                cliClassLoader.loadClasses();
                validateDriverIfApply();
                return new LauncherDefault(
                        isProfessional() ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp(),
                        cliClassLoader.getClassLoader(),
                        licenseKey,
                        driverWrapper
                );

            } else {
                throw new RuntimeException("Either an application jar or a jar containing the change units must be provided");
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
