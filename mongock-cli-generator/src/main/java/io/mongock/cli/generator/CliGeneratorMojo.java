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

    private static final String CLI_SCRIPT = "#!/bin/sh\n java -jar target/mongock-cli.jar \"$@\"\n";

    private static final String CLI_NAME = "target/mongock-cli.jar";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    //TODO implement for windows
    //todo test for mac
    public void execute() throws MojoExecutionException {
        try {
            generateCliArtifact();
            File file = getFileWithPermissions();
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(CLI_SCRIPT);
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating Mongock cli script", e);
        }
    }

    private void generateCliArtifact() throws IOException {
        String artifact = getArtifactPath();
        String cliName = getCliName();
        getLog().info("generating cli script for artifact: " +  artifact);
        execCommand(String.format("cp %s %s", artifact, cliName));
        String tempFile = execCommand("mktemp mongock.manifest.XXXXXXX --suffix \".txt\"");
        execCommand("echo \"Start-Class: io.mongock.professional.cli.springboot.MongockSpringbootCli\" >> " + tempFile);
        execCommand(String.format("jar ufm %s %s" , CLI_NAME, tempFile), false);
        execCommand("rm " + tempFile);
    }

    private String execCommand(String command) throws IOException {
        return execCommand(command, true);
    }

    private String execCommand(String command, boolean printError) throws IOException {
        String[] cpArgs = new String[]{"/bin/sh", "-c", command};
        Process process =  new ProcessBuilder(cpArgs).start();
        String error = getFromStream(process.getErrorStream());
        if(printError && StringUtils.isNotEmpty(error)) {
           getLog().error(error);
        }
        return getFromStream(process.getInputStream());
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
        return "target/mongock-cli.jar";
    }

    private String getArtifactPath() {
        String artifactTemplate = "%s/%s-%s.jar";
        String folder = "target";
        String artifactId = project.getArtifactId();
        String version = project.getVersion();
        return String.format(artifactTemplate, folder, artifactId, version);
    }
}
