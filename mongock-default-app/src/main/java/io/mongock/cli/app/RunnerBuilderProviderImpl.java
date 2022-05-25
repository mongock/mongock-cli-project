package io.mongock.cli.app;

import io.mongock.cli.app.events.MongockEventListener;
import io.mongock.runner.core.DriverWrapperReceiver;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;

public class RunnerBuilderProviderImpl implements RunnerBuilderProvider, DriverWrapperReceiver {

	private String driverWrapperName;

	@Override
	public RunnerBuilder getBuilder() {


		RunnerStandaloneBuilder runnerStandaloneBuilder = MongockStandalone.builder()
				.setDriver(DriverFactory.getDriver(driverWrapperName))
				.addMigrationScanPackage("io.mongock.examples.changelogs")
				.setMigrationStartedListener(MongockEventListener::onStart)
				.setMigrationSuccessListener(MongockEventListener::onSuccess)
				.setMigrationFailureListener(MongockEventListener::onFail)
				.setTrackIgnored(true)
				.setTransactionEnabled(true);
		return runnerStandaloneBuilder;
	}

	@Override
	public void setDriverWrapperName(String driverWrapperName) {
		this.driverWrapperName = driverWrapperName;
	}






}
