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

    public String cliSpringboot() {
        return String.format("%s/mongock-cli-springboot-%s.jar", jarsLib, cliVersion);
    }

    public String cliCore() {
        return String.format("%s/mongock-cli-core-%s.jar", jarsLib, cliVersion);
    }

    public String runnerCore() {
        return String.format("%s/mongock-runner-core-%s.jar", jarsLib, mongockCommunityVersion);
    }

}
