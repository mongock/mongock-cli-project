package io.mongock.examples;

import io.mongock.driver.cli.wrapper.mongodb.springdata.v3.SpringDataMongoV3Wrapper;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.examples.events.MongockEventListener;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;

public class RunnerBuilderProviderImpl implements RunnerBuilderProvider {


	@Override
	public RunnerBuilder getBuilder() {


		SpringDataMongoV3Wrapper wrapper = new SpringDataMongoV3Wrapper();
		ConnectionDriver driver = wrapper.getDriver();


		System.out.println("DRIVER : "+ driver);
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


	//****************************



}
