package io.mongock.cli.app.professional;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.driver.api.driver.ConnectionDriver;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static ConnectionDriver getDriver(String driverWrapperName) {

        if(driverWrapperName == null || driverWrapperName.isEmpty()) {
            throw new RuntimeException("Driver must not be null");
        }
        switch (DriverWrapper.valueOf(driverWrapperName)) {
            case MONGODB_SPRING_DATA_V3:
                return new io.mongock.driver.cli.wrapper.mongodb.springdata.v3.SpringDataMongoV3DriverProvider().getDriver();
            case MONGODB_SPRING_DATA_V2:
                return new io.mongock.driver.cli.wrapper.mongodb.springdata.v2.SpringDataMongoV2DriverProvider().getDriver();
            default:
                throw new RuntimeException(String.format("%s not found", driverWrapperName));
        }
    }
}
