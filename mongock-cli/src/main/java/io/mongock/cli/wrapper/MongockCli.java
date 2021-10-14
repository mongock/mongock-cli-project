package io.mongock.cli.wrapper;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import io.mongock.cli.wrapper.util.ArgsUtil;
import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.mongock.cli.util.logger.CliLogger.Level.INFO;

public class MongockCli {

	private static final CliLogger logger = CliLoggerFactory.getLogger(MongockCli.class);
	public static final String LOG_LEVEL_ARG = "-logLevel";
	public static final String APP_JAR_ARG = "-appJar";
	public static final String CLI_SPRING_JAR_ARG = "-cliSpringJar";
	public static final String CLI_CORE_JAR_ARG = "-cliCoreJar";

	public static void main(String... args) throws Exception {

		CliLogger.Level logLevel = ArgsUtil.getOptionalParam(args, LOG_LEVEL_ARG)
				.map(CliLogger.Level::fromStringDefaultInfo)
				.orElse(INFO);
		CliLoggerFactory.setLevel(logLevel);

		String appJar = ArgsUtil.getParameter(args, APP_JAR_ARG);
		JarFileArchive jarArchive = new JarFileArchive(new File(appJar));
		JarFile appJarFile = new JarFile(appJar);


		String[] cleanArgs = ArgsUtil.getCleanArgs(args, APP_JAR_ARG, CLI_SPRING_JAR_ARG, CLI_CORE_JAR_ARG);
		if (JarUtil.isSpringApplication(jarArchive)) {
			URLClassLoader classLoader = URLClassLoader.newInstance(
					new URL[]{new URL(String.format(JarUtil.JAR_URL_TEMPLATE, appJar))},
					Thread.currentThread().getContextClassLoader()
			);
			new SpringbootLauncher(jarArchive, ArgsUtil.getParameter(args, CLI_SPRING_JAR_ARG))
					.loadClasses(appJarFile, classLoader)
					.launch(cleanArgs);


		} else {
			String cliCoreJar = ArgsUtil.getParameter(args, CLI_CORE_JAR_ARG);

			URLClassLoader classLoader = URLClassLoader.newInstance(
					new URL[]{
							new URL(String.format(JarUtil.JAR_URL_TEMPLATE, appJar)),
							new URL(String.format(JarUtil.JAR_URL_TEMPLATE, cliCoreJar))},
					Thread.currentThread().getContextClassLoader()
			);

			ClassLoaderUtil.loadJarClasses(appJarFile, classLoader);
			ClassLoaderUtil.loadJarClasses(new JarFile(cliCoreJar), classLoader);

			String mainClassName = JarUtil.getMainClass(jarArchive);
			logger.debug("Main class: " + mainClassName);
			Class<?> mainClass = classLoader.loadClass(mainClassName);
			logger.debug("Loaded Main class");
			if (mainClass.isAnnotationPresent(MongockCliConfiguration.class)) {
				MongockCliConfiguration ann = mainClass.getAnnotation(MongockCliConfiguration.class);
				Class runnerBuilderProviderClass = Class.forName("io.mongock.runner.core.builder.RunnerBuilderProvider", false, classLoader);

				Class<?> builderProviderImplClass = ann.sources()[0];

				// RunnerBuilder provider
				Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
				Object builderProvider = constructor.newInstance();
				Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
				Object runnerBuilder = getBuilderMethod.invoke(builderProvider);
				logger.debug("FINITO3 " + runnerBuilder);


				//CALLING MongockCLI
				logger.debug("(1)LOADING MongockCLI class");
				Class<?> mongockCliClass = Class.forName("io.mongock.cli.core.CliCoreRunner", false, classLoader);
				logger.debug("(1)SUCCESSFULLY LOADED MongockCLI class");


				logger.debug("(2)Obtaining builder setter");
				Method builderMethod = mongockCliClass.getDeclaredMethod("builder");
				builderMethod.setAccessible(true);
				Object cliBuilder = builderMethod.invoke(null);
				logger.debug("(2)Obtained cliBuilder");

				logger.debug("(3)Loading class RunnerBuilder");
				Class<?> runnerBuilderClass = Class.forName("io.mongock.runner.core.builder.RunnerBuilder", false, classLoader);
				logger.debug("(3)SUCCESSFULLY Loaded class RunnerBuilder");

				logger.debug("(4)Setting RunnerBuilder to MongockCli.builder");
				Method runnerBuilderSetter = cliBuilder.getClass().getDeclaredMethod("runnerBuilder", runnerBuilderClass);
				runnerBuilderSetter.setAccessible(true);
				runnerBuilderSetter.invoke(cliBuilder, runnerBuilder);
				logger.debug("(4)Successful set RunnerBuilder to MongockCli.builder");

				logger.debug("(5)Building CommandLine");
				Method buildMethod = cliBuilder.getClass().getDeclaredMethod("build");
				buildMethod.setAccessible(true);
				Object commandLine = buildMethod.invoke(cliBuilder);
				logger.debug("(5)Successful built commandLine " + commandLine);

				StringBuilder sb = new StringBuilder();
				Stream.of(cleanArgs).forEach(s -> sb.append(s).append(" "));
				logger.debug("(6)Executing CommandLine with args: " + sb.toString());
				Method executeMethod = commandLine.getClass().getDeclaredMethod("execute", String[].class);
				executeMethod.setAccessible(true);
				executeMethod.invoke(commandLine, new Object[]{cleanArgs});
				logger.debug("(6)Successful call to commandLine.execute()");


			} else {
				logger.debug("Main class " + mainClassName + " not annotated with MongockCliConfiguration");
			}


		}


	}


}
