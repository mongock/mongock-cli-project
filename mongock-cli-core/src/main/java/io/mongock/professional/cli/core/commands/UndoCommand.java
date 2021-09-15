package io.mongock.professional.cli.core.commands;

import io.mongock.professional.runner.common.executor.operation.undo.UndoOp;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import static io.mongock.professional.cli.core.commands.CommandName.UNDO;

@Command(name = UNDO, description = "Reverts the applied changelogs", mixinStandardHelpOptions = true, version = "1.0")
public class UndoCommand extends CommandBase<Integer> {

    @CommandLine.Parameters(index = "0", description = "Not inclusive changelog up to which Mongock will rollback the change.")
    private String changelogId;

    public UndoCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call( ) {
        MongockRunner mongockRunner = builder.buildRunner(new UndoOp(changelogId));
        mongockRunner.forceEnable();
        mongockRunner.execute();
        return CommandLine.ExitCode.OK;
    }
}
