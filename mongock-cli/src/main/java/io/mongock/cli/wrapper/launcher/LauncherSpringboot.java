package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.launcher.springboot.CliMainMethodRunner;
import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.MainMethodRunner;
import org.springframework.boot.loader.archive.Archive;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.jar.JarFile;

public class LauncherSpringboot extends JarLauncher implements LauncherCliJar {


	private static final CliLogger logger = CliLoggerFactory.getLogger(LauncherSpringboot.class);
	public static final String BOOT_CLASSPATH_INDEX_ATTRIBUTE = JarLauncher.BOOT_CLASSPATH_INDEX_ATTRIBUTE;
	private static final String SPRING_CLI_MAIN_CLASS = "io.mongock.cli.springboot.CliSpringbootRunner";
	private static final String CLASS_EXT = ".class";
	private static final String SPRINGBOOT_PREFIX = "org/springframework/boot";

	private final String cliJarPath;
	private final String cliMainClass;
	private final String appJar;

	public LauncherSpringboot(Archive archive, String appJar, String cliJarPath) {
		super(archive);
		this.appJar = appJar;
		this.cliJarPath = cliJarPath;
		this.cliMainClass = SPRING_CLI_MAIN_CLASS;
	}


	public String getOriginalMainClass() {
		try {
			return super.getMainClass();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public LauncherSpringboot loadClasses() {
		try {
			URLClassLoader classLoader = URLClassLoader.newInstance(
					new URL[]{new URL(String.format(JarUtil.JAR_URL_TEMPLATE, appJar))},
					Thread.currentThread().getContextClassLoader()
			);

			ClassLoaderUtil.loadJarClasses(
					new JarFile(appJar),
					classLoader,
					entryName -> entryName.startsWith(SPRINGBOOT_PREFIX) && entryName.endsWith(CLASS_EXT));
			return this;
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
				new URL[]{new URL("jar:file:" + cliJarPath + "!/")},
				super.createClassLoader(archives));
	}
}
