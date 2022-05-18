package io.mongock.examples;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.examples.events.MongockEventListener;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;

public class RunnerBuilderProviderImpl implements RunnerBuilderProvider {

	private ConnectionDriver driver;

	public void setDriver(ConnectionDriver driver) {
		this.driver = driver;
	}


	@Override
	public RunnerBuilder getBuilder() {
		// Runner
		RunnerStandaloneBuilder runnerStandaloneBuilder = MongockStandalone.builder()
				.setDriver(driver)
				.addMigrationScanPackage("io.mongock.examples.changelogs")
				.setMigrationStartedListener(MongockEventListener::onStart)
				.setMigrationSuccessListener(MongockEventListener::onSuccess)
				.setMigrationFailureListener(MongockEventListener::onFail)
				.setTrackIgnored(true)
				.setTransactionEnabled(true);
		return runnerStandaloneBuilder;
	}

}
