package io.mongock.professional.cli.core.commands;

import io.mongock.professional.runner.common.executor.operation.undo.UndoOp;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.operation.change.MigrationOp;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import static io.mongock.professional.cli.core.commands.CommandName.UNDO;

@Command(name = UNDO, description = "Executes all the pending changes", mixinStandardHelpOptions = true, version = "1.0")
public class MigrateCommand extends CommandBase<Integer> {


    @SuppressWarnings("rawtypes")
    public MigrateCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call( ) {
        MongockRunner mongockRunner = builder.buildRunner(new MigrationOp());
        mongockRunner.forceEnable();
        mongockRunner.execute();
        return CommandLine.ExitCode.OK;
    }
}
