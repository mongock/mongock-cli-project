package io.mongock.cli.generator;


import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.PACKAGE)
public class CliGeneratorMojo extends AbstractMojo {


    private static final String TARGET_FOLDER = "target/";
    private static final String MONGOCK_FOLDER = "mongock";
    private static final String TEMP_MANIFEST_FILE = TARGET_FOLDER + MONGOCK_FOLDER + "/mongock.manifest.XXXXXXX";
    private static final String CLI_JAR_NAME_TEMPLATE = TARGET_FOLDER + MONGOCK_FOLDER + "/%s-mongock-cli.jar";
    private static final String CLI_LINUX_SCRIPT_TEMPLATE = "#!/bin/sh\n" +
            "#Generated cli script for application: %s\n" +
            " java -jar %s \"$@\"\n";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private File target;

    public void execute() throws MojoExecutionException {
        try {
            getLog().info("Generating Mongock cli");
            String appJar = getJarPath();//target/app.jar
            String cliJar = getCliName();//target/mongock/[app-name]-mongock-cli.jar
            if(!createMongockFolder()) {
                throw new MojoExecutionException("Error creating Mongock cli script: Mongock folder CANNOT be created");
            }
            generateCliArtifact(appJar, cliJar);
            File file = getFileWithPermissions();
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(String.format(CLI_LINUX_SCRIPT_TEMPLATE, appJar, cliJar));
            writer.close();
            getLog().info("Finished Mongock cli generation");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating Mongock cli script", e);
        }
    }


    private boolean createMongockFolder() throws MojoExecutionException {
        if(target.exists()) {
            getLog().info("target folder created");
        } else {
            getLog().error("target folder NOT created at the time of Mongock folder creation");
        }
        File mongockFolder = new File(target, MONGOCK_FOLDER);
        return mongockFolder.exists() || mongockFolder.mkdir();
    }

    private void generateCliArtifact(String appJar, String cliJar) throws IOException {
        getLog().debug("Generating cli script for artifact: " +  appJar);
        execCommand(String.format("cp %s %s", appJar, cliJar));
        String tempFile = execCommand("mktemp " + TEMP_MANIFEST_FILE);
        execCommand("echo \"Start-Class: io.mongock.professional.cli.springboot.MongockSpringbootCli\" >> " + tempFile);
        String cliJarName = String.format(CLI_JAR_NAME_TEMPLATE, getArtifactName());
        execCommand(String.format("jar ufm %s %s" , cliJarName, tempFile), false);
        execCommand("rm " + tempFile);
    }

    private String execCommand(String command) throws IOException {
        return execCommand(command, true);
    }

    private String execCommand(String command, boolean printError) throws IOException {

        getLog().debug("Executing command: " +  command);
        String[] cpArgs = new String[]{"/bin/sh", "-c", command};
        Process process =  new ProcessBuilder(cpArgs).start();
        String error = getFromStream(process.getErrorStream());
        if(printError && StringUtils.isNotEmpty(error)) {
           getLog().error(error);
        }

        String output = getFromStream(process.getInputStream());
        getLog().debug("Finished execution command(" +  command + ") with output: " + output);
        return output;
    }

    private String getFromStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder builder = new StringBuilder();
        String line;
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        return builder.toString();
    }


    private File getFileWithPermissions() throws IOException {
        File file = new File("mongock");
        file.createNewFile();
        Set<PosixFilePermission> perms = new HashSet<>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        perms.add(PosixFilePermission.GROUP_READ);
        perms.add(PosixFilePermission.GROUP_EXECUTE);
        perms.add(PosixFilePermission.OTHERS_READ);
        perms.add(PosixFilePermission.OTHERS_EXECUTE);
        Files.setPosixFilePermissions(file.toPath(), perms);
        return file;
    }

    private String getCliName() {
        return String.format(CLI_JAR_NAME_TEMPLATE, getArtifactName());
    }


    private String getArtifactName() {
        String artifactTemplate = "%s-%s";
        String artifactId = project.getArtifactId();
        String version = project.getVersion();
        return String.format(artifactTemplate, artifactId, version);
    }
    private String getArtifactPath() {
        return "target/" + getArtifactName();
    }

    private String getJarPath() {
        return getArtifactPath() + ".jar";
    }
}
