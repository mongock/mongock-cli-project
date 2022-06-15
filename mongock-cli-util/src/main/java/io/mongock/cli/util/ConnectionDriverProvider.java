package io.mongock.cli.util;

import io.mongock.driver.api.driver.ConnectionDriver;

public interface ConnectionDriverProvider {
    ConnectionDriver getDriver();
}
