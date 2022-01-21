package io.mongock.cli.wrapper.util;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.cli.wrapper.launcher.LauncherSpringboot;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class JarUtil {

	private static final CliLogger logger = CliLoggerFactory.getLogger(JarUtil.class);

	public  static final String JAR_URL_TEMPLATE = "jar:file:%s!/";
	private JarUtil() {}

	public static String getMainClass(JarFileArchive archive) throws IOException {
		String mainClass = getAttributes(archive).getValue(Attributes.Name.MAIN_CLASS);
		logger.debug("%s main class: %s", archive.getUrl(), mainClass);
		return mainClass;
	}

	public static boolean isSpringApplication(JarFileArchive archive) {
		try {
			return getAttributes(archive).getValue(LauncherSpringboot.BOOT_CLASSPATH_INDEX_ATTRIBUTE) != null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Attributes getAttributes(JarFileArchive archive) throws IOException {
		Manifest manifest = archive.getManifest();
		if(manifest == null) {
			throw new RuntimeException("manifest not present in the appJar");
		}
		Attributes attributes = manifest.getMainAttributes();
		if(attributes == null) {
			throw new RuntimeException("Mongock CLI cannot access to attributes in manifest");
		}
		return attributes;
	}

}
