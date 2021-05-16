package io.mongock.professional.cli.core.commands;

import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.professional.cli.core.commands.base.CommunityCommandBase;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "list")
public class ListCommand extends CommunityCommandBase {

    public ListCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call(RunnerBuilder builder) {
        System.out.println("This command lists all the changes with state and more information");
        return CommandLine.ExitCode.OK;
    }
}
