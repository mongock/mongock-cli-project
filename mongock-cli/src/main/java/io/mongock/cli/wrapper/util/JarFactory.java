package io.mongock.cli.wrapper.util;

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

    public String runnerStandalone() {
        return String.format("%s/mongock-standalone-%s.jar", jarsLib, mongockCommunityVersion);
    }

    public String runnerStandaloneBase() {
        return String.format("%s/mongock-standalone-base-%s.jar", jarsLib, mongockCommunityVersion);
    }


}
