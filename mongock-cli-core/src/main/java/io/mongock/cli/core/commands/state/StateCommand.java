package io.mongock.cli.core.commands.state;

import io.mongock.api.exception.MongockException;
import io.mongock.cli.core.VersionProvider;
import io.mongock.professional.runner.common.executor.operation.state.StateOperationResult;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import io.mongock.runner.core.executor.MongockRunner;

import io.mongock.cli.core.commands.CommandBase;
import io.mongock.cli.core.commands.CommandName;
import io.mongock.cli.core.commands.ProfessionalOperationProxy;
import io.mongock.professional.runner.common.executor.operation.state.StateOperationResultItem;
import java.util.List;
import java.util.function.Consumer;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = CommandName.STATE,
        description = "",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class StateCommand extends CommandBase<Integer> {
  
  private static final String SUBCOMMAND_DB = "db";
  private static final String SUBCOMMAND_CODE_BASE = "code-base";
  private static final String SUBCOMMAND_COMPARE = "compare";
  
  @SuppressWarnings("rawtypes")
  public StateCommand(RunnerBuilder builder) {
    super(builder);
  }

  @Override
  public Integer execution() throws Exception {
    printUsage();
    return CommandLine.ExitCode.USAGE;
  }
  
  private int runStateOperation(Consumer<MigrationSuccessResult> successListener) {
    
    if (!checkProfessionalBuilder()) {
      return CommandLine.ExitCode.USAGE;
    }
    
    MongockRunner mongockRunner = builder
            .setEventPublisher(new EventPublisher(null, successListener, null))
            .buildRunner(ProfessionalOperationProxy.stateOperation());
    mongockRunner.forceEnable();
    mongockRunner.execute();

    return CommandLine.ExitCode.OK;
  }
  
  private void processResult(MigrationSuccessResult result, Consumer<List<StateOperationResultItem>> consumer) {
    if (result == null || !(result.getResult() instanceof StateOperationResult)) {
      throw new MongockException("The operation has finished with invalid result.");
    } else {
      StateOperationResult stateOpResult = (StateOperationResult) result.getResult();
      if (stateOpResult.getItems().isEmpty()) {
        System.out.println("");
        System.out.println(">>> The operation has finished with empty result.");
        System.out.println("");
      } else {
        consumer.accept(stateOpResult.getItems());
      }
    }
  }
  
  @Command(name = SUBCOMMAND_DB,
          description = "(pro) - Show the current state of changes (./mongock " + CommandName.STATE + " " + SUBCOMMAND_DB + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer db() {
    return this.runStateOperation(result -> processResult(result, StateCommandHelper::printDbTable));
  }
  
  @Command(name = SUBCOMMAND_CODE_BASE,
          description = "(pro) - List the existing code changeUnits and their current state (./mongock " + CommandName.STATE + " " + SUBCOMMAND_CODE_BASE + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer codeBase() {
    return this.runStateOperation(result -> processResult(result, StateCommandHelper::printCodeBaseTable));
  }
  
  @Command(name = SUBCOMMAND_COMPARE,
          description = "(pro) - Compare the existing code changeUnits with the current state of changes (./mongock " + CommandName.STATE + " " + SUBCOMMAND_COMPARE + " -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer compare() {
    return this.runStateOperation(result -> processResult(result, StateCommandHelper::printCompareTable));
  }
}
