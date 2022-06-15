package io.mongock.cli.app;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.driver.api.driver.ConnectionDriver;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static ConnectionDriver getDriver(DriverWrapper driverWrapper) {

        switch (driverWrapper) {
            case MONGODB_SPRING_DATA_V3:
                return new io.mongock.driver.cli.wrapper.mongodb.springdata.v3.SpringDataMongoV3DriverProvider().getDriver();
            case MONGODB_SPRING_DATA_V2:
                return new io.mongock.driver.cli.wrapper.mongodb.springdata.v2.SpringDataMongoV2DriverProvider().getDriver();
            default:
                throw new RuntimeException(String.format("%s not found", driverWrapper.name()));
        }
    }
}
