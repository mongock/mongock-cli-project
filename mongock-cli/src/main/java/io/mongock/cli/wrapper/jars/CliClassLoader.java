package io.mongock.cli.wrapper.jars;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class CliClassLoader {
    private static final CliLogger logger = CliLoggerFactory.getLogger(CliClassLoader.class);
    private static final String CLASS_EXT = ".class";
    private static final String SPRINGBOOT_PREFIX = "org/springframework/boot";

    private static final Function<String, Boolean> springBootEntryFilter = entryName ->
            entryName.startsWith(SPRINGBOOT_PREFIX) && entryName.endsWith(CLASS_EXT);

    private static final Function<String, Boolean> nonSpringBootEntryFilter = entryName -> entryName.endsWith(CLASS_EXT);


    private final List<Jar> jars = new ArrayList<>();
    private URLClassLoader classLoader;

    public CliClassLoader addJar(Jar jar) {
        jars.add(jar);
        return this;
    }

    public CliClassLoader addJars(List<Jar> jars) {
        jars.addAll(jars);
        return this;
    }

    public void loadClasses() {
        URLClassLoader classLoader = getClassLoader();
        jars.forEach(jar -> CliClassLoader.loadJarClasses(jar, classLoader));
    }

    public static void loadJarClasses(Jar jar, URLClassLoader classLoader) {
        try {
            JarFile appJarFile = jar.getJarFile();
            logger.debug("loading jar: %s, with classLoader %s", appJarFile.getName(), classLoader.getClass().getName());
            Thread.currentThread().getContextClassLoader();
            Enumeration<JarEntry> jarEntryEnum = appJarFile.entries();
            Function<String, Boolean> jarEntryFilter = jar.isSpringApplication()
                    ? springBootEntryFilter
                    : nonSpringBootEntryFilter;

            while (jarEntryEnum.hasMoreElements()) {
                String entryName = jarEntryEnum.nextElement().getName();


                if (jarEntryFilter.apply(entryName)) {
                    String className = entryName.substring(0, entryName.lastIndexOf(CLASS_EXT)).replace('/', '.');
                    try {
                        classLoader.loadClass(className);
                    } catch (Throwable e) {
                        logger.warn(String.format("%s not loaded(%s)", className, e.getMessage()));
                    }
                    logger.trace("Loaded: " + className);
                }
            }
            appJarFile.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public URLClassLoader getClassLoader() {
        if (classLoader == null) {
            classLoader = URLClassLoader.newInstance(
                    jars.stream().map(Jar::getUrl).toArray(URL[]::new),
                    Thread.currentThread().getContextClassLoader()
            );
        }
        return classLoader;
    }
}
