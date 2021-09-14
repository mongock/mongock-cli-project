package io.mongock.professional.cli.core.commands;

import io.mongock.runner.core.builder.RunnerBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "list")
public class ListCommand extends CommandBase<Integer> {

    public ListCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call() {
        throw new UnsupportedOperationException("This operation will available in coming versions");
    }
}
