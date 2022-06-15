package io.mongock.cli.app;

import io.mongock.cli.app.events.MongockEventListener;
import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.util.DriverWrapperReceiver;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;

public class RunnerBuilderProviderImpl implements RunnerBuilderProvider, DriverWrapperReceiver {

    private DriverWrapper driverWrapper;

    @Override
    public RunnerBuilder getBuilder() {

        return MongockStandalone.builder()
                .setDriver(DriverFactory.getDriver(driverWrapper))
                .addMigrationScanPackage("io.mongock.examples.changelogs")
                .setMigrationStartedListener(MongockEventListener::onStart)
                .setMigrationSuccessListener(MongockEventListener::onSuccess)
                .setMigrationFailureListener(MongockEventListener::onFail)
                .setTrackIgnored(true)
                .setTransactionEnabled(true);
    }

    @Override
    public void setDriverWrapper(DriverWrapper driverWrapper) {
        this.driverWrapper = driverWrapper;
    }


}
