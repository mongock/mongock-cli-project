package io.mongock.cli.wrapper.launcher;

import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;

public interface CliJarLauncher {
	enum Type{SPRINGBOOT, STANDALONE};

	Type getType();

	CliJarLauncher cliJar(String jar);


	CliJarLauncher loadClasses();

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

		public  CliJarLauncher build() throws IOException {
			JarFileArchive archive = new JarFileArchive(new File(appJarFile));
			return JarUtil.isSpringApplication(archive)
					? new SpringbootLauncher(archive, appJarFile)
					: new StandaloneLauncher(archive, appJarFile);
		}

	}

}
