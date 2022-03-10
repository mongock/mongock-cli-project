package io.mongock.cli.core.commands.undo;

import io.mongock.cli.core.VersionProvider;
import io.mongock.cli.core.commands.CommandBase;
import io.mongock.cli.core.commands.CommandName;
import io.mongock.cli.core.commands.ProfessionalOperationProxy;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.executor.MongockRunner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = CommandName.UNDO,
        description = "",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class UndoCommand extends CommandBase<Integer> {

  private static final String SUBCOMMAND_ALL = "all";
  private static final String SUBCOMMAND_UP_TO_CHANGE = "up-to-change";

  @SuppressWarnings("rawtypes")
  public UndoCommand(RunnerBuilder builder) {
    super(builder);
  }

  @Override
  public Integer execution() throws Exception {
    printUsage();
    return CommandLine.ExitCode.USAGE;
  }
  
  @Command(name = SUBCOMMAND_ALL,
          description = "(pro) - Reverts all the applied changeUnits (./mongock " + CommandName.UNDO + " " + SUBCOMMAND_ALL + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer all() {
    MongockRunner mongockRunner = builder.buildRunner(ProfessionalOperationProxy.undoAllOperation());
    mongockRunner.forceEnable();
    mongockRunner.execute();
    return CommandLine.ExitCode.OK;
  }

  @Command(name = SUBCOMMAND_UP_TO_CHANGE,
          description = "(pro) - Reverts the applied migration up to specified changeUnitId (./mongock " + CommandName.UNDO + " " + SUBCOMMAND_UP_TO_CHANGE + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer upToChange(
          @Parameters(
                  index = "0",
                  description = "ChangeUnit id up to which Mongock will rollback(inclusive).",
                  paramLabel = "<CHANGE_UNIT_ID>") String changeUnitId) {
    MongockRunner mongockRunner = builder.buildRunner(ProfessionalOperationProxy.undoUpToChangeOperation(changeUnitId));
    mongockRunner.forceEnable();
    mongockRunner.execute();
    return CommandLine.ExitCode.OK;
  }
}
