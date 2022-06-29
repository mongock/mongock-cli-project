package io.mongock.cli.wrapper.jars;

import io.mongock.cli.wrapper.launcher.LauncherSpringboot;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class Jar {

    public  static final String JAR_URL_TEMPLATE = "jar:file:%s!/";
    private final String jarPath;

    public Jar(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getPath() {
        return jarPath;
    }

    public URL getUrl() {
        try {
            return new URL(String.format(JAR_URL_TEMPLATE, jarPath));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public JarFile getJarFile() {
        try {
            return new JarFile(jarPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JarFileArchive getJarFileArchive() {
        try {
            return new JarFileArchive(new File(jarPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getMainClass(ClassLoader classLoader) throws ClassNotFoundException {
        String className = getMainClassName();
        return classLoader.loadClass(className);
    }

    public String getMainClassName() {
        JarFileArchive archive = getJarFileArchive();
        String className = getAttributes(archive).getValue(Attributes.Name.MAIN_CLASS);
        return className;
    }

    public boolean isSpringApplication() {
        JarFileArchive archive = getJarFileArchive();
        return getAttributes(archive).getValue(LauncherSpringboot.BOOT_CLASSPATH_INDEX_ATTRIBUTE) != null;
    }

    private static Attributes getAttributes(JarFileArchive archive) {
        try {
            Manifest manifest = archive.getManifest();
            if(manifest == null) {
                throw new RuntimeException("manifest not present in the appJar");
            }
            Attributes attributes = manifest.getMainAttributes();
            if(attributes == null) {
                throw new RuntimeException("Mongock CLI cannot access to attributes in manifest");
            }
            return attributes;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
