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
									  URLClassLoader classLoader,
									  Function<String, Boolean> jarEntryFilter) throws Exception {
		Enumeration<JarEntry> jarEntryEnum = appJarFile.entries();
		while (jarEntryEnum.hasMoreElements()) {
			String entryName = jarEntryEnum.nextElement().getName();
			if (jarEntryFilter.apply(entryName)) {
				String className = entryName.substring(0, entryName.lastIndexOf(CLASS_EXT)).replace('/', '.');
				classLoader.loadClass(className);
			}
		}
		appJarFile.close();
	}
}
