package io.mongock.cli.wrapper;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.core.MongockCli;
import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import io.mongock.runner.core.builder.RunnerBuilderProvider;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class MongockCliMain {


	private static final String JAR_URL_TEMPLATE = "jar:file:%s!/";

	public static void main(String... args) throws Exception {

		String appJar = ArgsUtil.getParameter(args, "-appJar");
		JarFileArchive jarArchive = new JarFileArchive(new File(appJar));
		String cliJar = ArgsUtil.getParameter(args, "-cliJar");
		JarFile appJarFile = new JarFile(appJar);
		URLClassLoader classLoader = URLClassLoader.newInstance(
				new URL[]{new URL(String.format(JAR_URL_TEMPLATE, appJar))}
		);


		if(JarUtil.isSpringApplication(jarArchive)) {
			new SpringbootLauncher(jarArchive, cliJar)
					.loadSpringJar(appJarFile, classLoader)
					.launch(ArgsUtil.getCleanArgs(args, "-appJar", "-cliJar"));
		} else {

			ClassLoaderUtil.loadJarClasses(appJarFile, classLoader);
			String mainClassName = JarUtil.getMainClass(jarArchive);
			System.out.println("Main class: " + mainClassName);
			Class<?> mainClass = classLoader.loadClass(mainClassName);
			System.out.println("Loaded Main class");
			if(mainClass.isAnnotationPresent(MongockCliConfiguration.class)) {
				MongockCliConfiguration ann = mainClass.getAnnotation(MongockCliConfiguration.class);
				Class<? extends RunnerBuilderProvider> runnerBuilderProviderClass = (Class<? extends RunnerBuilderProvider>)classLoader.loadClass("io.mongock.runner.core.builder.RunnerBuilderProvider");

				Class<?> builderProviderImplClass = ann.sources()[0];
				Constructor<?> constructor = builderProviderImplClass.getDeclaredConstructor();
				Object  builderProvider = constructor.newInstance();
				Method getBuilderMethod = builderProvider.getClass().getMethod("getBuilder");
				Object builder = getBuilderMethod.invoke(builderProvider);
//				RunnerBuilder builder = builderProvider.getBuilder();
				System.out.println("FINITO3 " + builder);

				MongockCli
						.builder()
						.factory(null)
						.runnerBuilder(null)
						.build();

			} else {
				System.out.println("Main class " + mainClassName +" not annotated with MongockCliConfiguration");
			}


		}





	}




}
