package io.mongock.cli.core.commands.migrate;

import io.mongock.cli.core.VersionProvider;
import io.mongock.cli.core.commands.CommandBase;
import io.mongock.cli.core.commands.CommandName;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.operation.change.MigrationOp;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = CommandName.MIGRATE,
        description = "",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class MigrateCommand extends CommandBase<Integer> {

  private static final String SUBCOMMAND_ALL = "all";

  @SuppressWarnings("rawtypes")
  public MigrateCommand(RunnerBuilder builder) {
    super(builder);
  }

  @Override
  public Integer execution() throws Exception {
    printUsage();
    return CommandLine.ExitCode.USAGE;
  }

  @Command(name = SUBCOMMAND_ALL,
          description = "Executes all the pending changes (./mongock " + CommandName.MIGRATE + " " + SUBCOMMAND_ALL + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer all() {
    MongockRunner mongockRunner = builder.buildRunner(new MigrationOp());
    mongockRunner.forceEnable();
    mongockRunner.execute();
    return CommandLine.ExitCode.OK;
  }
}
