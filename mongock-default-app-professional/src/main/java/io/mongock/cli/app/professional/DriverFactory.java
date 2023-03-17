package io.mongock.cli.app.professional;

import io.mongock.cli.util.CliConfiguration;
import io.mongock.cli.util.ConnectionDriverProvider;
import io.mongock.cli.util.DriverWrapper;
import io.mongock.driver.api.driver.ConnectionDriver;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static ConnectionDriver getDriver(DriverWrapper driverWrapper, CliConfiguration configuration) {

        ConnectionDriverProvider connectionDriverProvider;
        switch (driverWrapper) {
            case MONGODB_SPRING_DATA_V3:
                connectionDriverProvider = new io.mongock.driver.cli.wrapper.mongodb.springdata.v3.SpringDataMongoV3DriverProvider();
                break;
            case MONGODB_SPRING_DATA_V2:
                connectionDriverProvider = new io.mongock.driver.cli.wrapper.mongodb.springdata.v2.SpringDataMongoV2DriverProvider();
                break;
            default:
                throw new RuntimeException(String.format("%s not found", driverWrapper));
        }
        return connectionDriverProvider.getDriver(configuration);
    }
}
