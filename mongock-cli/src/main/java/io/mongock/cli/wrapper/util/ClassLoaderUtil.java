package io.mongock.cli.wrapper.util;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;

import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassLoaderUtil {

    private static final CliLogger logger = CliLoggerFactory.getLogger(ClassLoaderUtil.class);

    private static final String CLASS_EXT = ".class";

    public static void loadJarClasses(JarFile appJarFile, URLClassLoader classLoader) {
        loadJarClasses(appJarFile, classLoader, e -> e.endsWith(CLASS_EXT));
    }

    public static void loadJarClasses(JarFile appJarFile, URLClassLoader classLoader, Function<String, Boolean> jarEntryFilter) {
        try {
            logger.debug("loading jar: %s, with classLoader %s", appJarFile.getName(), classLoader.getClass().getName());
            Thread.currentThread().getContextClassLoader();
            Enumeration<JarEntry> jarEntryEnum = appJarFile.entries();
            while (jarEntryEnum.hasMoreElements()) {
                String entryName = jarEntryEnum.nextElement().getName();
                if (jarEntryFilter.apply(entryName)) {
                    String className = entryName.substring(0, entryName.lastIndexOf(CLASS_EXT)).replace('/', '.');
                    try {
                        classLoader.loadClass(className);
                    } catch (Throwable e) {
                        logger.warn(String.format("%s not loaded(%s)", className, e.getMessage()));
                    }
                    logger.trace(className + " loaded ");
                }
            }
            appJarFile.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
