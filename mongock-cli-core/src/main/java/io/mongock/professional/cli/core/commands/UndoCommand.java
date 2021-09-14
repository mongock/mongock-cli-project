package io.mongock.professional.cli.core.commands;

import io.mongock.professional.runner.common.executor.operation.undo.UndoOp;
import io.mongock.runner.core.builder.RunnerBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "undo", description = "Reverts the applied changelogs")
public class UndoCommand extends CommandBase<Integer> {

    @Option(names = {"-c", "--changeLogId"},
            required = true,
            description = "Not inclusive changelog up to which Mongock will rollback the changes")
    private String changelogId;

    public UndoCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call( ) {
        builder.buildRunner(new UndoOp(changelogId)).execute();
        return CommandLine.ExitCode.OK;
    }
}
