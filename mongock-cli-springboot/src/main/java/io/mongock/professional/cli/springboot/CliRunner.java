package io.mongock.professional.cli.springboot;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import io.mongock.professional.cli.core.MongockCli;
import picocli.CommandLine;

public class CliRunner implements CommandLineRunner, ExitCodeGenerator {
    private final CommandLine.IFactory factory;

    private int exitCode;

    public CliRunner(CommandLine.IFactory factory) {
        this.factory = factory;
    }

    @Override
    public void run(String... args) {
        exitCode = MongockCli.builder()
                .factory(factory)
                .allCommands()
                .build()
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
