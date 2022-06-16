package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.DefaultAppConfiguration;
import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.Jar;
import org.jetbrains.annotations.NotNull;

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
    protected Object getRunnerBuilder(Class<?> builderProviderImplClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Class.forName("io.mongock.runner.core.builder.RunnerBuilderProvider", false, classLoader);

        Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
        Object builderProvider = constructor.newInstance();

        // setting configuration
        Method setConfigMethod = builderProvider.getClass().getMethod("setConfiguration", DefaultAppConfiguration.class);
        DefaultAppConfiguration defaultAppConfiguration = buildDefaultAppConfiguration();
        setConfigMethod.invoke(builderProvider, defaultAppConfiguration);

        Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
        Object builder = getBuilderMethod.invoke(builderProvider);

        //set licenseKey, if provided
//        if (licenseKey != null) {
//            Class.forName("io.mongock.professional.runner.common.RunnerBuilderProfessional", false, classLoader);
//
//
//            Method setLicenseKeyMethod = builder.getClass().getMethod("setLicenseKey", String.class);
//            setLicenseKeyMethod.invoke(builder, licenseKey);
//        }
        return builder;
    }

    @NotNull
    private DefaultAppConfiguration buildDefaultAppConfiguration() {
        DefaultAppConfiguration defaultAppConfiguration = new DefaultAppConfiguration();
        defaultAppConfiguration.setDriverWrapper(driverWrapper);
        defaultAppConfiguration.setLicenseKey(licenseKey);
        return defaultAppConfiguration;
    }

}
