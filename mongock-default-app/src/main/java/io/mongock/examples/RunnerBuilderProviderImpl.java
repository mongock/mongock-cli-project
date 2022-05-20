package io.mongock.examples;

import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.examples.events.MongockEventListener;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import io.mongock.runner.standalone.MongockStandalone;
import io.mongock.runner.standalone.RunnerStandaloneBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RunnerBuilderProviderImpl implements RunnerBuilderProvider {

	private Object connectionProvider;

	public void setDriverProvider(Object connectionProvider) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

		this.connectionProvider = connectionProvider;



	}


	@Override
	public RunnerBuilder getBuilder() {
		// Runner

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

}
