package io.mongock.cli.wrapper.springboot;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.wrapper.MongockCliMain;
import org.springframework.boot.loader.MainMethodRunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class CliMainMethodRunner extends MainMethodRunner {
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
		Stream.of(sources)
				.forEach(source -> System.out.println("Added source: " + source));
		Method method = cliMainClass.getDeclaredMethod("setSources", Class[].class);
		method.setAccessible(true);
		method.invoke(null, new Object[] { sources });
	}

	private Class<?>[] getSources(Class<?> originalMainClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		System.out.println("Sources added with " + MongockCliConfiguration.class.getSimpleName());

		Annotation annotation = originalMainClass.getAnnotation(MongockCliConfiguration.class);
		Method sourcesMethod = annotation.getClass().getDeclaredMethod("sources");
		return (Class<?>[])sourcesMethod.invoke(annotation);
	}

}
