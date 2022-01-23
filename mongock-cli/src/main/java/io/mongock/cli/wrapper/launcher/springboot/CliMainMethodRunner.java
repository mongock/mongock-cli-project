package io.mongock.cli.wrapper.launcher.springboot;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import org.springframework.boot.loader.MainMethodRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CliMainMethodRunner extends MainMethodRunner {

	private static final CliLogger logger = CliLoggerFactory.getLogger(CliMainMethodRunner.class);

	private final String cliMainClass;
	private final String originalMainClass;
	private final String[] mainArgs;

	public CliMainMethodRunner(String cliMainClass, String originalMainClass, String[] args) {
		super(cliMainClass, args);//these parent's fields are ignored as they are private
		this.cliMainClass = cliMainClass;
		this.originalMainClass = originalMainClass;
		this.mainArgs = (args != null) ? args.clone() : null;
	}

	public void run() throws Exception {
		Class<?> mainClass = Class.forName(this.cliMainClass, false, Thread.currentThread().getContextClassLoader());
		Class<?> originalMainClass = Class.forName(this.originalMainClass, false, Thread.currentThread().getContextClassLoader());
		setSources(mainClass, originalMainClass);
		runMainMethod(mainClass, this.mainArgs);
	}

	private void runMainMethod(Class<?> mainClass, String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method mainMethod = mainClass.getDeclaredMethod("main", String[].class);
		mainMethod.setAccessible(true);
		mainMethod.invoke(null, new Object[] { args });
	}

	private void setSources(Class<?> cliMainClass, Class<?> originalMainClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
		Class<?>[] sources = originalMainClass.isAnnotationPresent(MongockCliConfiguration.class)
				? getSources(originalMainClass)
				: new Class<?>[]{originalMainClass};
		Method method = cliMainClass.getDeclaredMethod("setSources", Class[].class);
		method.setAccessible(true);
		method.invoke(null, new Object[] { sources });
	}

	private Class<?>[] getSources(Class<?> originalMainClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		logger.debug("Sources added with " + MongockCliConfiguration.class.getSimpleName());
		Annotation annotation = originalMainClass.getAnnotation(MongockCliConfiguration.class);
		Method sourcesMethod = annotation.getClass().getDeclaredMethod("sources");
		return (Class<?>[])sourcesMethod.invoke(annotation);
	}

}
