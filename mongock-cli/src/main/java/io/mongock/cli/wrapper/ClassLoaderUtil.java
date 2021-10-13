package io.mongock.cli.wrapper;

import io.mongock.cli.wrapper.springboot.SpringbootLauncher;

import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassLoaderUtil {

	private static final String CLASS_EXT = ".class";

	public static void loadJarClasses(JarFile appJarFile,
									  URLClassLoader classLoader) throws Exception {
		loadJarClasses(appJarFile, classLoader, e -> e.endsWith(CLASS_EXT));
	}

	public static void loadJarClasses(JarFile appJarFile,
									  URLClassLoader classLoader,
									  Function<String, Boolean> jarEntryFilter) throws Exception {
		Thread.currentThread().getContextClassLoader();
		Enumeration<JarEntry> jarEntryEnum = appJarFile.entries();
		while (jarEntryEnum.hasMoreElements()) {
			String entryName = jarEntryEnum.nextElement().getName();
			if (jarEntryFilter.apply(entryName)) {
				String className = entryName.substring(0, entryName.lastIndexOf(CLASS_EXT)).replace('/', '.');
				try {
					classLoader.loadClass(className);
				} catch (NoClassDefFoundError e) {
					System.err.println("[warning] not loaded class(not found)" + className);
				}
			}
		}
		appJarFile.close();
	}
}
