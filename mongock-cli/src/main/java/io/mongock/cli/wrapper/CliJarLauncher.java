package io.mongock.cli.wrapper;

public interface CliJarLauncher {
	enum Type{SPRINGBOOT, STANDALONE};

	Type getType();

	CliJarLauncher cliJar(String jar);

	CliJarLauncher appJar(String jar);

	CliJarLauncher loadClasses();

	void launch(String[] args);

}
