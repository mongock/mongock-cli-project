package io.mongock.cli.wrapper.launcher;

import io.mongock.api.annotations.MongockCliConfiguration;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.util.ClassLoaderUtil;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class LauncherDefault extends LauncherStandalone {

	private static final CliLogger logger = CliLoggerFactory.getLogger(LauncherDefault.class);
	private final String driverWrapperJar;
	private final String runnerStandaloneJar;
	private final String runnerStandaloneBaseJar;


	public LauncherDefault(JarFileArchive appArchive,
						   String appJar,
						   String cliJar,
						   String runnerStandaloneBaseJar,
						   String runnerStandaloneJar,
						   String driverWrapperJar) {
		super(appArchive, appJar, cliJar);
		this.driverWrapperJar = driverWrapperJar;
		this.runnerStandaloneJar = runnerStandaloneJar;
		this.runnerStandaloneBaseJar = runnerStandaloneBaseJar;

	}

	@Override
	public LauncherCliJar loadClasses() {
		loadClassesInternal(driverWrapperJar, runnerStandaloneJar, runnerStandaloneBaseJar);
		return this;
	}

}
