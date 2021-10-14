package io.mongock.cli.wrapper;

import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import io.mongock.cli.wrapper.standalone.StandaloneLauncher;
import io.mongock.cli.wrapper.util.JarUtil;
import org.springframework.boot.loader.archive.JarFileArchive;

public final class LauncherFactory {

	private LauncherFactory() {}

	public static CliJarLauncher getLauncher(JarFileArchive archive) {
		return JarUtil.isSpringApplication(archive)
				? new SpringbootLauncher(archive)
				: new StandaloneLauncher(archive);
	}

}
