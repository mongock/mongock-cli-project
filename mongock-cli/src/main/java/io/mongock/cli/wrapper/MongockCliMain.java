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


	public static final String CLI_MAIN_CLASS = "io.mongock.cli.springboot.MongockSpringbootCommandLine";
	private static final String CLASS_EXT = ".class";
	private static final String SPRINGBOOT_PREFIX = "org/springframework/boot";
	private static final String JAR_URL_TEMPLATE = "jar:file:%s!/";

	public static void main(String... args) throws Exception {

		String appJar = getParameter(args, "-appJar");
		String cliJar = getParameter(args, "-cliJar");

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
		new SpringbootLauncher(jarArchive, cliJar, CLI_MAIN_CLASS).launch(getCleanArgs(args, "-appJar", "-cliJar"));
	}

	private static String[] getCleanArgs(String[] args, String... paramNames) {

		Set<String> paramNamesSet = Stream.of(paramNames)
				.map(String::toLowerCase)
				.collect(Collectors.toSet());

		String[] newArgs = new String[args.length - (paramNames.length * 2)];
		int newArgsIndex = 0;
		for (int i = 0; i < args.length; i++) {
			if (!paramNamesSet.contains(args[i].toLowerCase())) {
				newArgs[newArgsIndex++] = args[i];
			} else {
				i++;
			}
		}
		return newArgs;
	}

	private static String getParameter(String[] args, String paramName) {
		int i = 0;
		do {
			if (paramName.equalsIgnoreCase(args[i])) {
				if (args.length == i + 1) {
					throw new RuntimeException(
							String.format("Found [%s] flag with missing value. Please follow the format \"%s value\"", paramName, paramName)
					);
				}
				return args[i + 1];
			}
		} while ((++i) < args.length);
		throw new RuntimeException(String.format("Missing jar parameter. Please follow the format \"-%s jar_path\"", paramName));
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
