package io.mongock.cli.wrapper;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class MongockCliMain {


	private static final String JAR_URL_TEMPLATE = "jar:file:%s!/";

	public static void main(String... args) throws Exception {

		String appJar = ArgsUtil.getParameter(args, "-appJar");
		JarFileArchive jarArchive = new JarFileArchive(new File(appJar));
		JarFile appJarFile = new JarFile(appJar);


		String[] cleanArgs = ArgsUtil.getCleanArgs(args, "-appJar", "-cliSpringJar", "-cliCoreJar");
		if(JarUtil.isSpringApplication(jarArchive)) {
			URLClassLoader classLoader = URLClassLoader.newInstance(
					new URL[]{new URL(String.format(JAR_URL_TEMPLATE, appJar))},
					Thread.currentThread().getContextClassLoader()
			);
			new SpringbootLauncher(jarArchive, ArgsUtil.getParameter(args, "-cliSpringJar"))
					.loadSpringJar(appJarFile, classLoader)
					.launch(cleanArgs);


		} else {
			String cliCoreJar = ArgsUtil.getParameter(args, "-cliCoreJar");

			URLClassLoader classLoader = URLClassLoader.newInstance(
					new URL[]{
							new URL(String.format(JAR_URL_TEMPLATE, appJar)),
							new URL(String.format(JAR_URL_TEMPLATE, cliCoreJar))},
					Thread.currentThread().getContextClassLoader()
			);

			ClassLoaderUtil.loadJarClasses(appJarFile, classLoader);
			ClassLoaderUtil.loadJarClasses( new JarFile(cliCoreJar), classLoader);
			String mainClassName = JarUtil.getMainClass(jarArchive);
			System.out.println("Main class: " + mainClassName);
			Class<?> mainClass = classLoader.loadClass(mainClassName);
			System.out.println("Loaded Main class");
			if(mainClass.isAnnotationPresent(MongockCliConfiguration.class)) {
				MongockCliConfiguration ann = mainClass.getAnnotation(MongockCliConfiguration.class);
				Class runnerBuilderProviderClass = Class.forName("io.mongock.runner.core.builder.RunnerBuilderProvider", false, classLoader);

				Class<?> builderProviderImplClass = ann.sources()[0];

				// RunnerBuilder provider
				Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
				Object  builderProvider = constructor.newInstance();
				Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
				Object runnerBuilder = getBuilderMethod.invoke(builderProvider);
				System.out.println("FINITO3 " + runnerBuilder);


				//CALLING MongockCLI
				System.out.println("(1)LOADING MongockCLI class");
				Class<?> mongockCliClass = Class.forName("io.mongock.cli.core.MongockCli", false, classLoader);
				System.out.println("(1)SUCCESSFULLY LOADED MongockCLI class");


				System.out.println("(2)Obtaining builder setter");
				Method builderMethod = mongockCliClass.getDeclaredMethod("builder");
				builderMethod.setAccessible(true);
				Object cliBuilder = builderMethod.invoke(null);
				System.out.println("(2)Obtained cliBuilder");

				System.out.println("(3)Loading class RunnerBuilder");
				Class<?> runnerBuilderClass = Class.forName("io.mongock.runner.core.builder.RunnerBuilder", false, classLoader);
				System.out.println("(3)SUCCESSFULLY Loaded class RunnerBuilder");

				System.out.println("(4)Setting RunnerBuilder to MongockCli.builder");
				Method runnerBuilderSetter = cliBuilder.getClass().getDeclaredMethod("runnerBuilder", runnerBuilderClass);
				runnerBuilderSetter.setAccessible(true);
				runnerBuilderSetter.invoke(cliBuilder, runnerBuilder);
				System.out.println("(4)Successful set RunnerBuilder to MongockCli.builder");

				System.out.println("(5)Building CommandLine");
				Method buildMethod = cliBuilder.getClass().getDeclaredMethod("build");
				buildMethod.setAccessible(true);
				Object commandLine = buildMethod.invoke(cliBuilder);
				System.out.println("(5)Successful built commandLine " + commandLine);

				StringBuilder sb = new StringBuilder();
				Stream.of(cleanArgs).forEach(s -> sb.append(s).append(" "));
				System.out.println("(6)Executing CommandLine with args: " + sb.toString());
				Method executeMethod = commandLine.getClass().getDeclaredMethod("execute", String[].class);
				executeMethod.setAccessible(true);
				executeMethod.invoke(commandLine,  new Object[] { cleanArgs });
				System.out.println("(6)Successful call to commandLine.execute()");


			} else {
				System.out.println("Main class " + mainClassName +" not annotated with MongockCliConfiguration");
			}


		}





	}




}
