package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.jars.Jar;
import io.mongock.cli.wrapper.launcher.springboot.CliMainMethodRunner;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.MainMethodRunner;
import org.springframework.boot.loader.archive.Archive;

import java.net.URL;
import java.util.Iterator;

public class LauncherSpringboot extends JarLauncher implements LauncherCliJar {


	private static final CliLogger logger = CliLoggerFactory.getLogger(LauncherSpringboot.class);
	public static final String BOOT_CLASSPATH_INDEX_ATTRIBUTE = JarLauncher.BOOT_CLASSPATH_INDEX_ATTRIBUTE;
	private static final String SPRING_CLI_MAIN_CLASS = "io.mongock.cli.springboot.CliSpringbootRunner";

	private final Jar cliJar;
	private final String cliMainClass;

	public LauncherSpringboot(Jar appJar, Jar cliJar) {
		super(appJar.getJarFileArchive());
		this.cliJar = cliJar;
		this.cliMainClass = SPRING_CLI_MAIN_CLASS;
	}


	public String getOriginalMainClass() {
		try {
			return super.getMainClass();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getMainClass() {
		return cliMainClass;
	}

	@Override
	public void launch(String[] args) {
		try {
			logger.info("launching Mongock CLI runner with Springboot launcher");
			super.launch(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
		return new CliMainMethodRunner(mainClass, getOriginalMainClass(), args);
	}

	@Override
	protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
		return new LaunchedURLClassLoader(
				this.isExploded(),
				this.getArchive(),
				new URL[]{cliJar.getUrl()},
				super.createClassLoader(archives));
	}
}
