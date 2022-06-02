package io.mongock.cli.wrapper.util;

import java.util.Arrays;
import java.util.Collections;
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
        return Collections.singletonList(
                String.format("%s/mongock-default-app-professional-%s.jar", jarsLib, cliVersion)
        );
    }


}
