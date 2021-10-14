package io.mongock.cli.wrapper.springboot;

import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.MainMethodRunner;
import org.springframework.boot.loader.archive.Archive;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.jar.JarFile;

public class SpringbootLauncher extends JarLauncher {


	public static final String BOOT_CLASSPATH_INDEX_ATTRIBUTE = JarLauncher.BOOT_CLASSPATH_INDEX_ATTRIBUTE;
	private static final String SPRING_CLI_MAIN_CLASS = "io.mongock.cli.springboot.CliSpringbootRunner";
	private static final String CLASS_EXT = ".class";
	private static final String SPRINGBOOT_PREFIX = "org/springframework/boot";

	private final String cliJarPath;
	private final String cliMainClass;

	public SpringbootLauncher(Archive archive, String cliJarPath) {
		super(archive);
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

	public SpringbootLauncher loadClasses(JarFile appJarFile, URLClassLoader classLoader) throws Exception {
		ClassLoaderUtil.loadJarClasses(
				appJarFile,
				classLoader,
				entryName -> entryName.startsWith(SPRINGBOOT_PREFIX) && entryName.endsWith(CLASS_EXT));
		return this;
	}

	@Override
	protected String getMainClass() {
		return cliMainClass;
	}

	@Override
	public void launch(String[] args) throws Exception {
		super.launch(args);
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
