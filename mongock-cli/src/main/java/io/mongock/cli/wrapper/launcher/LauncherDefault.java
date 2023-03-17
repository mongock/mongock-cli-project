package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.CliConfiguration;
import io.mongock.cli.util.DriverWrapper;
import io.mongock.cli.wrapper.jars.Jar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LauncherDefault extends LauncherStandalone {

    private final DriverWrapper driverWrapper;

    private final String licenseKey;
    private final CliConfiguration cliConfiguration;


    public LauncherDefault(Jar appJar,
                           ClassLoader classLoader,
                           String licenseKey,
                           DriverWrapper driverWrapper,
                           CliConfiguration cliConfiguration
    ) {
        super(appJar, classLoader);
        this.licenseKey = licenseKey;
        this.driverWrapper = driverWrapper;
        this.cliConfiguration = cliConfiguration;

    }


    @Override
    protected Object getRunnerBuilder(Class<?> builderProviderImplClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {

        Class.forName("io.mongock.runner.core.builder.RunnerBuilderProvider", false, classLoader);

        Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
        Object builderProvider = constructor.newInstance();

        // setting configuration
        Method setConfigMethod = builderProvider.getClass().getMethod("setConfiguration", CliConfiguration.class);
        setConfigMethod.invoke(builderProvider, cliConfiguration);

        Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
        Object builder = getBuilderMethod.invoke(builderProvider);

        return builder;
    }



}
