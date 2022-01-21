package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import io.mongock.cli.wrapper.util.JarUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import static io.mongock.cli.wrapper.launcher.LauncherCliJar.Type.STANDALONE;

public class LauncherWithoutApp implements LauncherCliJar {

    private static final CliLogger logger = CliLoggerFactory.getLogger(LauncherWithoutApp.class);

    private String cliJar;

    private URLClassLoader classLoader;

    public LauncherWithoutApp() {
    }

    @Override
    public Type getType() {
        return STANDALONE;
    }

    @Override
    public LauncherCliJar cliJar(String cliJar) {
        this.cliJar = cliJar;
        return this;
    }

    @Override
    public LauncherCliJar loadClasses() {

        try {
            this.classLoader = buildClassLoader();
            ClassLoaderUtil.loadJarClasses(new JarFile(cliJar), classLoader);

            return this;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void launch(String[] args) {
        try {
            logger.info("launching Mongock CLI runner with Standalone launcher");

            Object cliBuilder = getCliBuilder();

            Object commandLine = buildCli(cliBuilder);

            executeCli(args, commandLine);


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private void executeCli(String[] args, Object commandLine) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        StringBuilder sb = new StringBuilder();
        Stream.of(args).forEach(s -> sb.append(s).append(" "));
        logger.debug("executing CommandLine with args: " + sb);
        Method executeMethod = commandLine.getClass().getDeclaredMethod("execute", String[].class);
        executeMethod.setAccessible(true);
        executeMethod.invoke(commandLine, new Object[]{args});
        logger.debug("successful call to commandLine.execute()");
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
                        new URL(String.format(JarUtil.JAR_URL_TEMPLATE, cliJar))},
                Thread.currentThread().getContextClassLoader()
        );
    }
}
