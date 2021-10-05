package io.mongock.springboot;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.MainMethodRunner;
import org.springframework.boot.loader.archive.Archive;

import java.net.URL;
import java.util.Iterator;

public class SpringbootLauncher extends JarLauncher {
	private final String cliJarPath;
	private final String cliMainClass;


	public SpringbootLauncher(Archive archive,
							  String cliJarPath,
							  String cliMainClass) {
		super(archive);
		this.cliJarPath = cliJarPath;
		this.cliMainClass = cliMainClass;
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
	public void launch(String[] args) throws Exception {
		super.launch(args);
	}

	@Override
	protected MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
		return new CliMainMethodRunner(mainClass, getOriginalMainClass(), args);
	}
	protected ClassLoader createClassLoader(Iterator<Archive> archives) throws Exception {
		return new LaunchedURLClassLoader(
				this.isExploded(),
				this.getArchive(),
				new URL[]{new URL("jar:file:" + cliJarPath + "!/")},
				super.createClassLoader(archives));

	}



}
