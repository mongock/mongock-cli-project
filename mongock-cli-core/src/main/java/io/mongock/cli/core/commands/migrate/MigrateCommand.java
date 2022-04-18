package io.mongock.cli.core.commands.migrate;

import io.mongock.cli.core.VersionProvider;
import io.mongock.cli.core.commands.CommandBase;
import io.mongock.cli.core.commands.CommandName;
import io.mongock.cli.core.commands.ProfessionalOperationProxy;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import io.mongock.runner.core.executor.operation.migrate.MigrateAllOperation;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = CommandName.MIGRATE,
        description = "",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class MigrateCommand extends CommandBase<Integer> {

  private static final String SUBCOMMAND_ALL = "all";
  private static final String SUBCOMMAND_UP_TO_CHANGE = "up-to-change";

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
    MongockRunner mongockRunner = builder.buildRunner(new MigrateAllOperation());
    mongockRunner.forceEnable();
    mongockRunner.execute();
    return CommandLine.ExitCode.OK;
  }
  
  @Command(name = SUBCOMMAND_UP_TO_CHANGE,
          description = "(pro) - Executes all the pending changes up to specified changeUnitId (./mongock " + CommandName.MIGRATE + " " + SUBCOMMAND_UP_TO_CHANGE + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer upToChange(
          @CommandLine.Parameters(
                  index = "0",
                  description = "ChangeUnit id up to which Mongock will execute migration(inclusive).",
                  paramLabel = "<CHANGE_UNIT_ID>") String changeUnitId) {
    if (!checkProfessionalBuilder()) {
      return CommandLine.ExitCode.USAGE;
    }
    MongockRunner mongockRunner = builder.buildRunner(ProfessionalOperationProxy.migrateUpToChangeOperation(changeUnitId));
    mongockRunner.forceEnable();
    mongockRunner.execute();
    return CommandLine.ExitCode.OK;
  }
}
