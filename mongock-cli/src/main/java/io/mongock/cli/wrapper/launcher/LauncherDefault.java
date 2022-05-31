package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DriverWrapper;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class LauncherDefault extends LauncherStandalone {

	private final DriverWrapper driverWrapper;
	private final List<String> extraJars;

	private final String licenseKey;


	public LauncherDefault(JarFileArchive appArchive,
						   String appJar,
						   String cliJar,
						   String licenseKey,
						   DriverWrapper driverWrapper,
						   List<String> extraJars) {
		super(appArchive, appJar, cliJar);
		this.licenseKey = licenseKey;
		this.driverWrapper = driverWrapper;
		this.extraJars = extraJars;

	}

	@Override
	public LauncherCliJar loadClasses() {
		ArrayList<String> allJars = new ArrayList<>();
		allJars.add(driverWrapper.getJar());
		allJars.addAll(extraJars);
		loadClassesInternal(allJars);
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
		Object builder = getBuilderMethod.invoke(builderProvider);

		//set licenseKey, if provided
		if(licenseKey != null) {
			try {
				Class.forName("io.mongock.professional.runner.common.RunnerBuilderProfessional", false, classLoader);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			Method setLicenseKeyMethod = builder.getClass().getMethod("setLicenseKey", String.class);
			System.out.println("\n\nSetting licenseKey:\n" + licenseKey+"\n\n");
			setLicenseKeyMethod.invoke(builder, licenseKey);
		}
		return builder;
	}

}
