package io.mongock.cli.wrapper.util;

import java.util.Arrays;
import java.util.List;

public class JarFactory {
    private final String jarsLib;
    private final String cliVersion;
    private final String mongockCommunityVersion;
    private final String mongockProVersion;


    public JarFactory(String jarsLib, String cliVersion, String mongockCommunityVersion, String mongockProVersion) {
        this.jarsLib = jarsLib;
        this.cliVersion = cliVersion;
        this.mongockCommunityVersion = mongockCommunityVersion;
        this.mongockProVersion = mongockProVersion;
    }

    public String defaultApp() {
        return String.format("%s/mongock-default-app-%s.jar", jarsLib, cliVersion);
    }

    public String cliSpringboot() {
        return String.format("%s/mongock-cli-springboot-%s.jar", jarsLib, cliVersion);
    }

    public String cliCore() {
        return String.format("%s/mongock-cli-core-%s.jar", jarsLib, cliVersion);
    }

    public List<String> runnerCommunityDependencies() {
        return Arrays.asList(
                String.format("%s/mongock-standalone-base-%s.jar", jarsLib, mongockCommunityVersion),
                String.format("%s/mongock-standalone-%s.jar", jarsLib, mongockCommunityVersion)
        );
    }

    //TODO generify versions
    public List<String> runnerProfessionalDependencies() {
        return Arrays.asList(
                String.format("%s/mongock-standalone-base-%s.jar", jarsLib, mongockCommunityVersion),

                String.format("%s/kotlin-stdlib-jdk8-%s.jar", jarsLib, "1.6.0"),
                String.format("%s/kotlin-stdlib-%s.jar", jarsLib, "1.6.0"),
                String.format("%s/kotlin-stdlib-jdk7-%s.jar", jarsLib, "1.6.0"),
                String.format("%s/kotlin-logging-jvm-%s.jar", jarsLib, "2.0.11"),
                String.format("%s/kotlin-stdlib-common-%s.jar", jarsLib, "1.5.21"),
                String.format("%s/jjwt-api-%s.jar", jarsLib, "0.11.1"),
                String.format("%s/jwt-api-%s.jar", jarsLib, "1.0.3"),
                String.format("%s/jwt-parser-%s.jar", jarsLib, "1.0.3"),
                String.format("%s/mongock-api-%s.jar", jarsLib, mongockProVersion),
                String.format("%s/mongock-runner-common-%s.jar", jarsLib, mongockProVersion),
                String.format("%s/mongock-standalone-%s.jar", jarsLib, mongockProVersion)
        );
    }


}
