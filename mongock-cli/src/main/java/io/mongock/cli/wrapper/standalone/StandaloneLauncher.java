package io.mongock.cli.wrapper.standalone;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.banner.Banner;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.CliJarLauncher;
import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.mongock.cli.wrapper.CliJarLauncher.Type.STANDALONE;

public class StandaloneLauncher implements CliJarLauncher {

	private static final CliLogger logger = CliLoggerFactory.getLogger(StandaloneLauncher.class);
	private final JarFileArchive appJarArchive;


	private String appJar;
	private String cliJar;

	private URLClassLoader classLoader;

	public StandaloneLauncher(JarFileArchive appArchive) {
		this.appJarArchive = appArchive;
	}

	@Override
	public Type getType() {
		return STANDALONE;
	}

	@Override
	public CliJarLauncher cliJar(String cliJar) {
		this.cliJar = cliJar;
		return this;
	}

	@Override
	public CliJarLauncher appJar(String appJar) {
		this.appJar = appJar;
		return this;
	}

	@Override
	public CliJarLauncher loadClasses() {

		try {
			this.classLoader = buildClassLoader();
			ClassLoaderUtil.loadJarClasses(new JarFile(appJar), classLoader);
			ClassLoaderUtil.loadJarClasses(new JarFile(cliJar), classLoader);
			
			return this;	
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	@Override
	public void launch(String[] args) {
		try {
			logger.info("launching Mongock CLI runner with Standalone launcher");
			String mainClassName = JarUtil.getMainClass(appJarArchive);
			Class<?> mainClass = getMainClass(mainClassName);
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
				throw new RuntimeException("Main class " + mainClassName + " not annotated with MongockCliConfiguration");
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private Class<?> getMainClass(String mainClassName) throws ClassNotFoundException {
		logger.debug("Main class: " + mainClassName);
		Class<?> mainClass = classLoader.loadClass(mainClassName);
		logger.debug("loaded Main class");
		return mainClass;
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

	private Object getRunnerBuilder(Class<?> builderProviderImplClass) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
		Object builderProvider = constructor.newInstance();
		Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
		return getBuilderMethod.invoke(builderProvider);
	}

	private URLClassLoader buildClassLoader() throws MalformedURLException {
		return URLClassLoader.newInstance(
				new URL[]{
						new URL(String.format(JarUtil.JAR_URL_TEMPLATE, appJar)),
						new URL(String.format(JarUtil.JAR_URL_TEMPLATE, cliJar))},
				Thread.currentThread().getContextClassLoader()
		);
	}
}