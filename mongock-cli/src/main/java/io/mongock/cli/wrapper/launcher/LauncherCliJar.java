package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.CliConfiguration;
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

        private CliConfiguration configuration;

//        private Jar userApplicationJar;
//        private Jar userChangeUnitJar;



        public LauncherBuilder(JarFactory jarFactory) {
            this.jarFactory = jarFactory;
        }

        public LauncherBuilder setConfiguration(CliConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        //        /**
//         * Optional setter if Mongock is run based on an application
//         *
//         * @param userApplicationJar application jar
//         * @return builder
//         */
//        public LauncherBuilder setUserApplicationJar(Jar userApplicationJar) {
//            this.userApplicationJar = userApplicationJar;
//            return this;
//        }

//        public LauncherBuilder setUserChangeUnitJar(Jar userChangeUnitJar) {
//            this.userChangeUnitJar = userChangeUnitJar;
//            return this;
//        }





        public LauncherCliJar build() {
            if (configuration.getUserApplication().isPresent()) {
                Jar userApplicationJar = new Jar(configuration.getUserApplication().get());
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
            } else if(configuration.getUserChangeUnit().isPresent())  {
                DriverWrapper driverWrapper = configuration.getDriverWrapper();
                CliClassLoader cliClassLoader = new CliClassLoader()
                        .addJar(isProfessional() ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp())
                        .addJar(jarFactory.cliCore())
                        .addJar(new Jar(driverWrapper.getJarPath()))
                        .addJars(isProfessional() ? jarFactory.runnerProfessionalDependencies() : jarFactory.runnerCommunityDependencies())
                        .addJar(new Jar(configuration.getUserChangeUnit().get()));
                cliClassLoader.loadClasses();
                validateDriverIfApply(driverWrapper);
                return new LauncherDefault(
                        isProfessional() ? jarFactory.defaultProfessionalApp() : jarFactory.defaultApp(),
                        cliClassLoader.getClassLoader(),
                        configuration.getLicenseKey().get(),
                        driverWrapper,
                        configuration
                );

            } else {
                throw new RuntimeException("Either an application jar or a jar containing the change units must be provided");
            }
        }

        private boolean isProfessional() {
            return configuration.getLicenseKey().isPresent();
        }

        private void validateDriverIfApply(DriverWrapper driverWrapper) {
            if (driverWrapper == null) {
                throw new RuntimeException(String.format(
                        "When application is missing, Parameter `%s` must be provided :  \n%s",
                        DRIVER.getDefaultName(),
                        DriverWrapper.getAllDriverNames("\n")));
            }
        }
    }

}
