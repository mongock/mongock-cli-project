package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import io.mongock.cli.wrapper.util.JarUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class LauncherDefaultOld implements LauncherCliJar {

    private static final CliLogger logger = CliLoggerFactory.getLogger(LauncherDefaultOld.class);

    private final String cliJarPath;

    private final String mongockCoreJarFile;

    private URLClassLoader classLoader;

    public LauncherDefaultOld(String mongockCoreJarFile, String cliJarPath) {
        this.mongockCoreJarFile = mongockCoreJarFile;
        this.cliJarPath = cliJarPath;
    }

    @Override
    public LauncherCliJar loadClasses() {

        try {
            this.classLoader = buildClassLoader();
            ClassLoaderUtil.loadJarClasses(new JarFile(mongockCoreJarFile), classLoader);
            ClassLoaderUtil.loadJarClasses(new JarFile(cliJarPath), classLoader);
            return this;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void launch(String[] args) {
        try {
            logger.info("launching Mongock CLI runner with default launcher");

            Object commandLine = buildCli(getCliBuilder());

            StringBuilder sb = new StringBuilder();
            Stream.of(args).forEach(s -> sb.append(s).append(" "));
            logger.debug("executing CommandLine with args: " + sb);
            Method executeMethod = commandLine.getClass().getDeclaredMethod("execute", String[].class);
            executeMethod.setAccessible(true);
            executeMethod.invoke(commandLine, new Object[]{args});
            logger.debug("successful call to commandLine.execute()");


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private Object buildCli(Object cliBuilder) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        logger.debug("building CommandLine");
        Method buildMethod = cliBuilder.getClass().getDeclaredMethod("build");
        buildMethod.setAccessible(true);
        Object commandLine = buildMethod.invoke(cliBuilder);
        logger.debug("successful built commandLine " + commandLine);
        return commandLine;
    }


    private Object getCliBuilder() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        logger.debug("loading MongockCLI class");
        Class<?> mongockCliClass = Class.forName("io.mongock.cli.core.CliCoreRunner", false, classLoader);
        logger.debug("successfully loaded MongockCLI class");


        logger.debug("obtaining builder setter");
        Method builderMethod = mongockCliClass.getDeclaredMethod("builder");
        builderMethod.setAccessible(true);
        Object cliBuilder = builderMethod.invoke(null);
        logger.debug("obtained cliBuilder");
        return cliBuilder;
    }


    private URLClassLoader buildClassLoader() throws MalformedURLException {
        return URLClassLoader.newInstance(
                new URL[]{
                        new URL(String.format(JarUtil.JAR_URL_TEMPLATE, mongockCoreJarFile)),
                        new URL(String.format(JarUtil.JAR_URL_TEMPLATE, cliJarPath))
                },
                Thread.currentThread().getContextClassLoader()
        );
    }
}
