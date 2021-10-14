package io.mongock.cli.wrapper;

public interface JarLauncher {

	JarLauncher addJar(String jar);

	JarLauncher loadClasses();

	void launch(String[] args);

}
