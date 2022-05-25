package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LauncherDefault extends LauncherStandalone {

	private final DriverWrapper driverWrapper;
	private final String runnerStandaloneJar;
	private final String runnerStandaloneBaseJar;


	public LauncherDefault(JarFileArchive appArchive,
						   String appJar,
						   String cliJar,
						   String runnerStandaloneBaseJar,
						   String runnerStandaloneJar,
						   DriverWrapper driverWrapper) {
		super(appArchive, appJar, cliJar);
		this.driverWrapper = driverWrapper;
		this.runnerStandaloneJar = runnerStandaloneJar;
		this.runnerStandaloneBaseJar = runnerStandaloneBaseJar;

	}

	@Override
	public LauncherCliJar loadClasses() {
		loadClassesInternal(driverWrapper.getJar(), runnerStandaloneJar, runnerStandaloneBaseJar);
		return this;
	}

	@Override
	protected Object getRunnerBuilder(Class<?> builderProviderImplClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
		Object builderProvider = constructor.newInstance();

		// setting the DriverWrapperName
		Method setDriverWrapperNameMethod = builderProvider.getClass().getMethod("setDriverWrapperName", String.class);
		setDriverWrapperNameMethod.invoke(builderProvider, driverWrapper.name());


		Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
		return getBuilderMethod.invoke(builderProvider);
	}

}
