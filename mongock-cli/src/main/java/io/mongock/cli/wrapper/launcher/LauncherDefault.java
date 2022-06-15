package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.Jar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LauncherDefault extends LauncherStandalone {

	private final DriverWrapper driverWrapper;

	private final String licenseKey;


	public LauncherDefault(Jar appJar,
						   ClassLoader classLoader,
						   String licenseKey,
						   DriverWrapper driverWrapper
						   ) {
		super(appJar, classLoader);
		this.licenseKey = licenseKey;
		this.driverWrapper = driverWrapper;

	}


	@Override
	protected Object getRunnerBuilder(Class<?> builderProviderImplClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
		Object builderProvider = constructor.newInstance();

		// setting the DriverWrapperName
		Method setDriverWrapperNameMethod = builderProvider.getClass().getMethod("setDriverWrapper", DriverWrapper.class);
		setDriverWrapperNameMethod.invoke(builderProvider, driverWrapper);


		Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
		Object builder = getBuilderMethod.invoke(builderProvider);

		//set licenseKey, if provided
		if(licenseKey != null) {
			try {
				Class.forName("io.mongock.professional.runner.common.RunnerBuilderProfessional", false, classLoader);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			Method setLicenseKeyMethod = builder.getClass().getMethod("setLicenseKey", String.class);
			setLicenseKeyMethod.invoke(builder, licenseKey);
		}
		return builder;
	}

}
