package io.mongock.cli.app.professional;

import io.mongock.cli.app.professional.events.MongockEventListener;
import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.util.DriverWrapperReceiver;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;

public class RunnerBuilderProviderProfessionalImpl implements RunnerBuilderProvider, DriverWrapperReceiver {

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
    public void setDriverWrapperName(String driverWrapperName) {
        if (driverWrapperName == null || driverWrapperName.isEmpty()) {
            throw new RuntimeException("Driver must not be null");
        }
        this.driverWrapper = DriverWrapper.valueOf(driverWrapperName);
    }


}
