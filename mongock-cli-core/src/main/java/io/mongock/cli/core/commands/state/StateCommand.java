package io.mongock.cli.core.commands.state;

import io.mongock.api.exception.MongockException;
import io.mongock.cli.core.VersionProvider;
import io.mongock.professional.runner.common.executor.operation.state.StateOpResult;
import io.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.runner.core.event.EventPublisher;
import io.mongock.runner.core.event.result.MigrationSuccessResult;
import io.mongock.runner.core.executor.MongockRunner;

import io.mongock.cli.core.commands.CommandBase;
import io.mongock.cli.core.commands.CommandName;
import io.mongock.cli.core.commands.ProfessionalOperationProxy;
import io.mongock.professional.runner.common.executor.operation.state.StateOpResultItem;
import java.util.List;
import java.util.function.Consumer;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(name = CommandName.STATE,
        description = "(pro) - Show the current state of changes (./mongock state -h for more details)",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class StateCommand extends CommandBase<Integer> {
  
  @SuppressWarnings("rawtypes")
  public StateCommand(RunnerBuilder builder) {
    super(builder);
  }

  @Override
  public Integer call() {
    return CommandLine.ExitCode.USAGE;
  }
  
  private int runStateOperation(Consumer<MigrationSuccessResult> successListener) {
    
    MongockRunner mongockRunner = builder
            .setEventPublisher(new EventPublisher(null, successListener, null))
            .buildRunner(ProfessionalOperationProxy.stateOp());
    mongockRunner.forceEnable();
    mongockRunner.execute();

    return CommandLine.ExitCode.OK;
  }
  
  private void processResult(MigrationSuccessResult result, Consumer<List<StateOpResultItem>> consumer) {
    if (result == null || !(result.getResult() instanceof StateOpResult)) {
      throw new MongockException("The operation has finished with invalid result.");
    } else {
      StateOpResult stateOpResult = (StateOpResult) result.getResult();
      if (stateOpResult.getItems().isEmpty()) {
        System.out.println("");
        System.out.println(">>> The operation has finished with empty result.");
        System.out.println("");
      } else {
        consumer.accept(stateOpResult.getItems());
      }
    }
  }
  
  @Command(name = "db",
          description = "(pro) - Show the current state of changes (./mongock state db -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer db() {
    return this.runStateOperation(result -> processResult(result, StateCommandHelper::printDbTable));
  }
  
  @Command(name = "code-base",
          description = "(pro) - List the existing code changeUnits and their current state (./mongock state code-base -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer codeBase() {
    return this.runStateOperation(result -> processResult(result, StateCommandHelper::printCodeBaseTable));
  }
  
  @Command(name = "compare",
          description = "(pro) - Compare the existing code changeUnits with the current state of changes (./mongock state compare -h for more details)",
          mixinStandardHelpOptions = true,
          versionProvider = VersionProvider.class)
  public Integer compare() {
    return this.runStateOperation(result -> processResult(result, StateCommandHelper::printCompareTable));
  }
}
