package io.mongock.cli.wrapper;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class MongockCliMain {


	private static final String JAR_URL_TEMPLATE = "jar:file:%s!/";

	public static void main(String... args) throws Exception {

		String appJar = ArgsUtil.getParameter(args, "-appJar");
		JarFileArchive jarArchive = new JarFileArchive(new File(appJar));
		String cliJar = ArgsUtil.getParameter(args, "-cliJar");
		JarFile AppJarFile = new JarFile(appJar);
		URLClassLoader classLoader = URLClassLoader.newInstance(
				new URL[]{new URL(String.format(JAR_URL_TEMPLATE, appJar))}
		);


		if(isSpringApplication(jarArchive)) {
			new SpringbootLauncher(jarArchive, cliJar)
					.loadSpringJar(AppJarFile, classLoader)
					.launch(ArgsUtil.getCleanArgs(args, "-appJar", "-cliJar"));
		} else {
			System.out.println("\n\nIT'S NOT A SPRING APPLICATION");
		}





	}

	private static boolean isSpringApplication(JarFileArchive archive) throws IOException {
		Manifest manifest = archive.getManifest();
		if(manifest == null) {
			throw new RuntimeException("manifest not present in the appJar");
		}
		Attributes attributes = manifest.getMainAttributes();
		if(attributes == null) {
			throw new RuntimeException("Mongock CLI cannot access to attributes in manifest");
		}
		return attributes.getValue(SpringbootLauncher.BOOT_CLASSPATH_INDEX_ATTRIBUTE) != null;
	}


}
