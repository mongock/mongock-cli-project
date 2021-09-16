package io.mongock.professional.cli.core.commands;

import io.mongock.runner.core.builder.RunnerBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import static io.mongock.professional.cli.core.commands.CommandName.LIST;

@Command(name = LIST)
public class ListCommand extends CommandBase<Integer> {

    public ListCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call() {
        throw new UnsupportedOperationException("This operation will available in coming versions");
    }
}
