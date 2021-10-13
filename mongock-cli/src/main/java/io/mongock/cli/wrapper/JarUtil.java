package io.mongock.cli.wrapper;

import io.mongock.cli.wrapper.springboot.SpringbootLauncher;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class JarUtil {

	private JarUtil() {}

	public static String getMainClass(JarFileArchive archive) throws IOException {
		return getAttributes(archive).getValue(Attributes.Name.MAIN_CLASS);
	}

	public static boolean isSpringApplication(JarFileArchive archive) throws IOException {
		return getAttributes(archive).getValue(SpringbootLauncher.BOOT_CLASSPATH_INDEX_ATTRIBUTE) != null;
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
