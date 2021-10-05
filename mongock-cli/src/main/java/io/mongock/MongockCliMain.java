package io.mongock;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import io.mongock.springboot.SpringbootLauncher;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MongockCliMain {

//	private static final Logger logger = LoggerFactory.getLogger(App.class);

	//TODO this should ba passed as parameter in the CLI
	private static String pathToSpringJar = "/home/dieppa/dev/SpringApp.jar";
	public static final String CLI_MAIN_CLASS = "io.mongock.cli.springboot.MongockSpringbootCommandLine";
	private static String CLI_JAR = "/home/dieppa/dev/cli.jar";


	private static final String CLASS_EXT = ".class";
	private static final String SPRINGBOOT_PREFIX = "org/springframework/boot";
	private static final String JAR_URL_TEMPLATE = "jar:file:%s!/";

	public static void main(String... args) throws Exception {

		JarFileArchive jarArchive = new JarFileArchive(new File(pathToSpringJar));

		// Class name to Class object mapping.
		URLClassLoader classLoader = URLClassLoader.newInstance(
				new URL[]{new URL(String.format(JAR_URL_TEMPLATE, pathToSpringJar))}
		);

		// Create the jar from the path and retrieves
		JarFile AppJarFile = new JarFile(pathToSpringJar);

		// Loads the spring's main components to be able to launch it
		loadSpringJar(AppJarFile.entries(), classLoader);

		AppJarFile.close();
		new SpringbootLauncher(jarArchive, CLI_JAR, CLI_MAIN_CLASS).launch(args);
	}



	public static void loadSpringJar(Enumeration<JarEntry> jarEntryEnum, URLClassLoader classLoader) throws Exception {

		while (jarEntryEnum.hasMoreElements()) {
			String entryName  = jarEntryEnum.nextElement().getName();
			if (entryName.startsWith(SPRINGBOOT_PREFIX) && entryName.endsWith(CLASS_EXT)) {
				String className = entryName.substring(0, entryName.lastIndexOf(CLASS_EXT)).replace('/', '.');
				Class<?> loadedClass = classLoader.loadClass(className);
//				logger.trace("...Loaded springboot base class: {}", loadedClass.getName());
			}
		}
	}


}
