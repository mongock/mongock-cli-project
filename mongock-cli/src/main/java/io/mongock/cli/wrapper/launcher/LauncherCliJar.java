package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.wrapper.util.JarFactory;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static io.mongock.cli.wrapper.util.Parameters.APP_JAR_ARG_LONG;
import static io.mongock.cli.wrapper.util.Parameters.CLI_CORE_JAR_ARG;
import static io.mongock.cli.wrapper.util.Parameters.CLI_SPRING_JAR_ARG;
import static io.mongock.cli.wrapper.util.Parameters.MONGOCK_CORE_JAR_ARG;

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

        private String mongockCoreJarFile;


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
            mongockCoreJarFile = jarFactory.runnerCore();

            if (getAppJar().isPresent()) {
                JarFileArchive archive = new JarFileArchive(new File(appJarFile));
                if (JarUtil.isSpringApplication(archive)) {
                    return buildLauncherSpring(archive);
                } else {
                    return buildLauncherStandalone(archive);
                }
            } else {
                return buildLauncherWithoutApp();
            }
        }

        private LauncherDefault buildLauncherWithoutApp() {
            validateNotNullParameter(mongockCoreJarFile, "parameter " + MONGOCK_CORE_JAR_ARG);
            validateNotNullParameter(cliCoreJar, "parameter " + CLI_CORE_JAR_ARG);
            return new LauncherDefault(mongockCoreJarFile, cliCoreJar);
        }

        private LauncherStandalone buildLauncherStandalone(JarFileArchive archive) {
            validateNotNullParameter(appJarFile, "parameter " + APP_JAR_ARG_LONG);
            validateNotNullParameter(cliCoreJar, "parameter " + CLI_CORE_JAR_ARG);
            return new LauncherStandalone(archive, appJarFile, cliCoreJar);
        }

        private LauncherSpringboot buildLauncherSpring(JarFileArchive archive) {
            validateNotNullParameter(appJarFile, "parameter " + APP_JAR_ARG_LONG);
            validateNotNullParameter(cliSpringJar, "parameter " + CLI_SPRING_JAR_ARG);
            return new LauncherSpringboot(archive, appJarFile, cliSpringJar);
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
