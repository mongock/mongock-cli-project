package io.mongock.cli.core.commands;

import io.mongock.cli.core.VersionProvider;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = CommandName.UNDO,
        description = "Reverts the applied migration (./mongock undo -h for more details)",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class UndoCommand extends CommandBase<Integer> {

    @Parameters(index = "0", description = "  ChangeUnit id   up to which Mongock will rollback(inclusive).")
    private String changeId;

    @SuppressWarnings("rawtypes")
    public UndoCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call( ) {
        MongockRunner mongockRunner = builder.buildRunner(ProfessionalOperationProxy.undoOp(changeId));
        mongockRunner.forceEnable();
        mongockRunner.execute();
        return CommandLine.ExitCode.OK;
    }
}
