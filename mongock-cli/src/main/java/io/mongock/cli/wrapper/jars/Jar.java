package io.mongock.cli.wrapper.jars;

import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;

public class Jar {

    public  static final String JAR_URL_TEMPLATE = "jar:file:%s!/";
    private final String jarPath;

    public Jar(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getPath() {
        return jarPath;
    }

    public String getUrl() {
        return String.format(JAR_URL_TEMPLATE, jarPath);
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
}
