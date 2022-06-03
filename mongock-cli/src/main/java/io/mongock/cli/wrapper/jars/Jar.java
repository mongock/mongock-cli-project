package io.mongock.cli.wrapper.jars;

import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class Jar {

    private final String jarPath;

    public Jar(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getUrl() {
        return jarPath;
    }


    public JarFile getJarFile() {
        return null;
    }

    public JarFileArchive getJarFileArchive() {
        try {
            return new JarFileArchive(new File(jarPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
