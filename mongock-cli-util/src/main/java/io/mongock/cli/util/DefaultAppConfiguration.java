package io.mongock.cli.util;

public class DefaultAppConfiguration {

    private DriverWrapper driverWrapper;


    public String getScanPackage() {
        return "io.mongock.examples.changelogs";
    }

    public DriverWrapper getDriverWrapper() {
        return driverWrapper;
    }

    public void setDriverWrapper(DriverWrapper driverWrapper) {
        this.driverWrapper = driverWrapper;
    }
}
