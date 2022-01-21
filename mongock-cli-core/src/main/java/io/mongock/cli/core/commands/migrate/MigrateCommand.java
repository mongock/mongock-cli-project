package io.mongock.cli.core.commands.migrate;

import io.mongock.cli.core.VersionProvider;
import io.mongock.cli.core.commands.CommandBase;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.operation.change.MigrationOp;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import static io.mongock.cli.core.commands.CommandName.MIGRATE;

@Command(name = MIGRATE,
        description = "Executes all the pending changes",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class MigrateCommand extends CommandBase<Integer> {


    @SuppressWarnings("rawtypes")
    public MigrateCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer execution() throws Exception {
        MongockRunner mongockRunner = builder.buildRunner(new MigrationOp());
        mongockRunner.forceEnable();
        mongockRunner.execute();
        return CommandLine.ExitCode.OK;
    }
}
