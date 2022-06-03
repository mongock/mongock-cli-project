package io.mongock.cli.wrapper.launcher;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.jars.Jar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class LauncherStandalone implements LauncherCliJar {

	private static final CliLogger logger = CliLoggerFactory.getLogger(LauncherStandalone.class);


	private final Jar appJar;

	protected final ClassLoader classLoader;

	public LauncherStandalone(Jar appJar, ClassLoader classLoader) {
		this.appJar = appJar;
		this.classLoader = classLoader;
	}

	@Override
	public void launch(String[] args) {
		try {
			logger.info("launching Mongock CLI runner with Standalone launcher");
			Class<?> mainClass = appJar.getMainClass(classLoader);
			if (mainClass.isAnnotationPresent(MongockCliConfiguration.class)) {
				MongockCliConfiguration ann = mainClass.getAnnotation(MongockCliConfiguration.class);
				Class.forName("io.mongock.runner.core.builder.RunnerBuilderProvider", false, classLoader);

				Class<?> builderProviderImplClass = ann.sources()[0];


				Object runnerBuilder = getRunnerBuilder(builderProviderImplClass);

				Object cliBuilder = getCliBuilder();

				setRunnerBuilderToCli(runnerBuilder, cliBuilder);

				Object commandLine = buildCli(cliBuilder);

				executeCli(args, commandLine);

			} else {
				throw new RuntimeException("Main class " + mainClass.getName() + " not annotated with MongockCliConfiguration");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}



	private void executeCli(String[] args, Object commandLine) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		StringBuilder sb = new StringBuilder();
		Stream.of(args).forEach(s -> sb.append(s).append(" "));
		logger.debug("executing CommandLine with args: " + sb);
		Method executeMethod = commandLine.getClass().getDeclaredMethod("execute", String[].class);
		executeMethod.setAccessible(true);
		executeMethod.invoke(commandLine, new Object[]{args});
		logger.debug("successful call to commandLine.execute()");
	}

	private Object buildCli(Object cliBuilder) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("building CommandLine");
		Method buildMethod = cliBuilder.getClass().getDeclaredMethod("build");
		buildMethod.setAccessible(true);
		Object commandLine = buildMethod.invoke(cliBuilder);
		logger.debug("successful built commandLine " + commandLine);
		return commandLine;
	}

	private void setRunnerBuilderToCli(Object runnerBuilder, Object cliBuilder) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("loading class RunnerBuilder");
		Class<?> runnerBuilderClass = Class.forName("io.mongock.runner.core.builder.RunnerBuilder", false, classLoader);
		logger.debug("successfully loaded class RunnerBuilder");

		logger.debug("setting RunnerBuilder to MongockCli.builder");
		Method runnerBuilderSetter = cliBuilder.getClass().getDeclaredMethod("runnerBuilder", runnerBuilderClass);
		runnerBuilderSetter.setAccessible(true);
		runnerBuilderSetter.invoke(cliBuilder, runnerBuilder);
		logger.debug("successfully set RunnerBuilder to MongockCli.builder");
	}


	private Object getCliBuilder() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		logger.debug("loading MongockCLI class");
		Class<?> mongockCliClass = Class.forName("io.mongock.cli.core.CliCoreRunner", false, classLoader);
		logger.debug("successfully loaded MongockCLI class");


		logger.debug("obtaining builder setter");
		Method builderMethod = mongockCliClass.getDeclaredMethod("builder");
		builderMethod.setAccessible(true);
		Object cliBuilder = builderMethod.invoke(null);
		logger.debug("obtained cliBuilder");
		return cliBuilder;
	}

	protected Object getRunnerBuilder(Class<?> builderProviderImplClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
		Object builderProvider = constructor.newInstance();
		Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
		return getBuilderMethod.invoke(builderProvider);
	}

}
