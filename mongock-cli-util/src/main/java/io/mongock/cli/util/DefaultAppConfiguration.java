package io.mongock.cli.util;

import java.util.Optional;

public class DefaultAppConfiguration {

    private DriverWrapper driverWrapper;

    private String licenseKey;


    public String getScanPackage() {
        return "io.mongock.examples.changelogs";
    }

    public DriverWrapper getDriverWrapper() {
        return driverWrapper;
    }

    public void setDriverWrapper(DriverWrapper driverWrapper) {
        this.driverWrapper = driverWrapper;
    }

    public Optional<String> getLicenseKey() {
        return Optional.ofNullable(licenseKey);
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
}
