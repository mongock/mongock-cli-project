package io.mongock.cli.wrapper.util;

public enum DriverWrapper {
    MONGODB_SPRING_DATA_V3("%s/mongodb-springdata-v3-wrapper-%s.jar"),
    MONGODB_SPRING_DATA_V2("%s/mongodb-springdata-v3-wrapper-%s.jar");

    private final String template;

    DriverWrapper(String template) {
        this.template = template;
    }

    String getJar(String jarsLib, String version) {
        return String.format(template, jarsLib, version);
    }


}
