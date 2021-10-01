package io.mongock.professional.cli.core.commands;

import io.mongock.professional.runner.common.executor.operation.undo.UndoOp;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import static io.mongock.professional.cli.core.commands.CommandName.UNDO;

@Command(name = UNDO, description = "Reverts the applied migration (./mongock undo -h for more details)", mixinStandardHelpOptions = true, version = "1.0")
public class UndoCommand extends CommandBase<Integer> {

    @Parameters(index = "0", description = "  ChangeUnit id   up to which Mongock will rollback(inclusive).")
    private String changeId;

    @SuppressWarnings("rawtypes")
    public UndoCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call( ) {
        MongockRunner mongockRunner = builder.buildRunner(new UndoOp(changeId));
        mongockRunner.forceEnable();
        mongockRunner.execute();
        return CommandLine.ExitCode.OK;
    }
}
