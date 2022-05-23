package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.wrapper.util.JarFactory;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static io.mongock.cli.wrapper.util.Parameters.APP_JAR_ARG_LONG;


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


        public LauncherBuilder() {
        }

        public LauncherBuilder setAppJarFile(String appJarFile) {
            this.appJarFile = appJarFile;
            return this;
        }

        public LauncherBuilder setJarFactory(JarFactory jarFactory) {
            this.jarFactory = jarFactory;
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
            return new LauncherDefault(
                    appArchive,
                    appJar,
                    cliCoreJar,
                    jarFactory.runnerStandaloneBase(),
                    jarFactory.runnerStandalone(),
                    jarFactory.mongoSpringDataV3Wrapper()//todo get this from a factory depending on driver parameter
                    );
        }

        private LauncherStandalone buildLauncherStandalone(JarFileArchive appArchive, String appJar) {
            validateNotNullParameter(appJar, "parameter " + APP_JAR_ARG_LONG);
            validateNotNullParameter(cliCoreJar, "library cli core jar ");
            return new LauncherStandalone(appArchive, appJar, cliCoreJar);
        }

        private LauncherSpringboot buildLauncherSpring(JarFileArchive appArchive, String appJar) {
            validateNotNullParameter(appJar, "parameter " + APP_JAR_ARG_LONG);
            validateNotNullParameter(cliSpringJar, "library cli spring jar ");
            return new LauncherSpringboot(appArchive, appJar, cliSpringJar);
        }


        private Optional<String> getAppJar() {
            return Optional.ofNullable(appJarFile);
        }

        private void validateNotNullParameter(Object parameter, String name) {
        	if(parameter == null) {
        		throw new RuntimeException(name + " must be provided");
			}
		}
    }

}
