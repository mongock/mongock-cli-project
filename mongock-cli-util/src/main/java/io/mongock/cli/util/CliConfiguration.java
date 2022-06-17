package io.mongock.cli.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class CliConfiguration {

    private String driverName;

    private String databaseUrl;

    private String databaseName;


    private String jarsLibFolder;

    private String cliVersion;

    private String scanPackage;

    private String licenseKey;

    private String userApplication;

    private String userChangeUnit;

    public static Builder fileBuilder() {
        return new Builder();
    }

    public CliConfiguration setScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        return this;
    }

    public String getScanPackage() {
        return scanPackage;
    }


    public CliConfiguration setUserAppIfNotNull(String userApplication) {
        this.userApplication = userApplication != null ? userApplication : this.userApplication;
        return this;
    }

    public Optional<String> getUserApplication() {
        return Optional.ofNullable(userApplication);
    }

    public Optional<String> getUserChangeUnit() {
        return Optional.ofNullable(userChangeUnit);
    }

    public CliConfiguration setUserChangeUnitIfNotNull(String userChangeUnit) {
        this.userChangeUnit = userChangeUnit != null ? userChangeUnit : this.userChangeUnit;
        return this;
    }

    public CliConfiguration setJarsLibFolder(String jarsLibFolder) {
        this.jarsLibFolder = jarsLibFolder;
        return this;
    }

    public CliConfiguration setCliVersion(String cliVersion) {
        this.cliVersion = cliVersion;
        return this;
    }


    public DriverWrapper getDriverWrapper() {
        return DriverWrapper.getDriver(driverName)
                .setJarsLibFolder(jarsLibFolder)
                .setVersion(cliVersion);
    }

    public CliConfiguration setDriverNameIfNotNull(String driverName) {
        this.driverName = driverName != null ? driverName : this.driverName;
        return this;
    }

    public Optional<String> getLicenseKey() {
        return Optional.ofNullable(licenseKey);
    }

    public CliConfiguration setLicenseKeyIfNotNull(String licenseKey) {
        this.licenseKey = licenseKey != null ? licenseKey : this.licenseKey;
        return this;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public CliConfiguration setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl != null ? databaseUrl : this.databaseUrl;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public CliConfiguration setDatabaseName(String databaseName) {
        this.databaseName = databaseName != null ? databaseName : this.databaseName;
        return this;
    }

    public static class Builder {

        private String configFile;

        private Builder() {
        }


        public Builder setConfigFile(String configFile) {
            this.configFile = configFile;
            return this;
        }


        public CliConfiguration build() {
            try {
                CliConfiguration config = new CliConfiguration();
                for (String configLine : Files.readAllLines(Paths.get(configFile))) {
                    if (configLine != null && !configLine.isEmpty()) {
                        String[] parts = configLine.split("=");
                        setConfigValue(config, parts[0], parts[1]);
                    }
                }
                return config;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private CliConfiguration setConfigValue(CliConfiguration config, String propertyName, String propertyValue) {
            switch (propertyName.toLowerCase()) {
                case "licensekey":
                    return config.setLicenseKeyIfNotNull(propertyValue);
                case "scanpackage":
                    return config.setScanPackage(propertyValue);
                case "driver":
                    return config.setDriverNameIfNotNull(propertyValue);
                case "databaseurl":
                    return config.setDatabaseUrl(propertyValue);
                case "databasename":
                    return config.setDatabaseName(propertyValue);

                default:
                    return config;//IGNORED
            }
        }

    }
}
