package io.mongock.cli.util;

public enum DriverWrapper {
    MONGODB_SPRING_DATA_V3("%s/mongodb-springdata-v3-wrapper-%s.jar"),
    MONGODB_SPRING_DATA_V2("%s/mongodb-springdata-v3-wrapper-%s.jar");

    private final String jarTemplate;

    DriverWrapper(String template) {
        this.jarTemplate = template;
    }

    public String getJar(String jarsLib, String version) {
        return String.format(jarTemplate, jarsLib, version);
    }


}
