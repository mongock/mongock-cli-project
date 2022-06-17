package io.mongock.cli.app;

import io.mongock.cli.app.events.MongockEventListener;
import io.mongock.cli.util.CliConfiguration;
import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.util.RunnerBuilderProviderConfigurable;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;

public class RunnerBuilderProviderImpl implements RunnerBuilderProvider, RunnerBuilderProviderConfigurable {

    private DriverWrapper driverWrapper;
    private CliConfiguration configuration;

    @Override
    public RunnerBuilder getBuilder() {

        return MongockStandalone.builder()
                .setDriver(DriverFactory.getDriver(driverWrapper, configuration))
                .addMigrationScanPackage(configuration.getScanPackage())
                .setMigrationStartedListener(MongockEventListener::onStart)
                .setMigrationSuccessListener(MongockEventListener::onSuccess)
                .setMigrationFailureListener(MongockEventListener::onFail)
                .setTrackIgnored(true)
                .setTransactionEnabled(true);
    }


    @Override
    public void setConfiguration(CliConfiguration configuration) {
        this.configuration = configuration;
        this.driverWrapper = configuration.getDriverWrapper();
    }


}
