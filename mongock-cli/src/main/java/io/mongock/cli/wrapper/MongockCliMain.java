package io.mongock.cli.wrapper;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MongockCliMain {

//	private static final Logger logger = LoggerFactory.getLogger(MongockCliMain.class);

	private static final String CLASS_EXT = ".class";
	private static final String SPRINGBOOT_PREFIX = "org/springframework/boot";
	private static final String JAR_URL_TEMPLATE = "jar:file:%s!/";

	public static void main(String... args) throws Exception {

		String appJar = ArgsUtil.getParameter(args, "-appJar");
		String cliJar = ArgsUtil.getParameter(args, "-cliJar");


		JarFileArchive jarArchive = new JarFileArchive(new File(appJar));

		// Class name to Class object mapping.
		URLClassLoader classLoader = URLClassLoader.newInstance(
				new URL[]{new URL(String.format(JAR_URL_TEMPLATE, appJar))}
		);

		// Create the jar from the path and retrieves
		JarFile AppJarFile = new JarFile(appJar);

		// Loads the spring's main components to be able to launch it
		loadSpringJar(AppJarFile.entries(), classLoader);
		AppJarFile.close();
		new SpringbootLauncher(jarArchive, cliJar).launch(ArgsUtil.getCleanArgs(args, "-appJar", "-cliJar"));
	}



	public static void loadSpringJar(Enumeration<JarEntry> jarEntryEnum, URLClassLoader classLoader) throws Exception {

		while (jarEntryEnum.hasMoreElements()) {
			String entryName = jarEntryEnum.nextElement().getName();
			if (entryName.startsWith(SPRINGBOOT_PREFIX) && entryName.endsWith(CLASS_EXT)) {
				String className = entryName.substring(0, entryName.lastIndexOf(CLASS_EXT)).replace('/', '.');
				Class<?> loadedClass = classLoader.loadClass(className);
//				logger.trace("...Loaded springboot base class: {}", loadedClass.getName());
			}
		}
	}


}
