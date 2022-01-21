package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface LauncherCliJar {
	enum Type{SPRINGBOOT, STANDALONE};

	Type getType();

	LauncherCliJar cliJar(String jar);


	LauncherCliJar loadClasses();

	void launch(String[] args);


	static LauncherBuilder builder() {
		return new LauncherBuilder();
	}

	class LauncherBuilder {

		private String appJarFile;

		public LauncherBuilder() {}

		public LauncherBuilder setAppJarFile(String appJarFile) {
			this.appJarFile = appJarFile;
			return this;
		}

		public LauncherCliJar build() {
			return getAppJar()
					.map(LauncherBuilder::getLauncherWithAppJar)
					.orElseGet(LauncherWithoutApp::new);
		}


		private Optional<String> getAppJar() {
			return Optional.ofNullable(appJarFile);
		}

		private static  LauncherCliJar getLauncherWithAppJar(String appJarFile) {
			try {
				JarFileArchive archive = new JarFileArchive(new File(appJarFile));
				return JarUtil.isSpringApplication(archive)
						? new LauncherSpringboot(archive, appJarFile)
						: new LauncherStandalone(archive, appJarFile);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

	}

}
