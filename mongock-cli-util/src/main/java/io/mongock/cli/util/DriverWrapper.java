package io.mongock.cli.util;

public enum DriverWrapper {
    MONGODB_SPRING_DATA_V3("%s/mongodb-springdata-v3-wrapper-%s.jar"),
    MONGODB_SPRING_DATA_V2("%s/mongodb-springdata-v2-wrapper-%s.jar");

    private final String jarTemplate;
    private String jarsLibFolder;
    private String version;

    DriverWrapper(String template) {
        this.jarTemplate = template;
    }

    public DriverWrapper setJarsLibFolder(String jarsLibFolder) {
        this.jarsLibFolder = jarsLibFolder;
        return this;
    }

    public DriverWrapper setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getJarPath() {
        return String.format(jarTemplate, jarsLibFolder, version);
    }

    public static DriverWrapper getDriver(String driverName) {
        return valueOf(driverName.toUpperCase());
    }


}
