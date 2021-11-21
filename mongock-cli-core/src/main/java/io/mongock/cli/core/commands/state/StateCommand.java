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

    MongockRunner mongockRunner = builder
            .setEventPublisher(new EventPublisher(null, this::migrationSuccessListener, null))
            .buildRunner(ProfessionalOperationProxy.stateOp());
    mongockRunner.forceEnable();
    mongockRunner.execute();
    return CommandLine.ExitCode.OK;
  }

  private void migrationSuccessListener(MigrationSuccessResult result) {
    if (result == null || !(result.getResult() instanceof StateOpResult)) {
      throw new MongockException("The operation has finished with invalid result.");
    } else {
      StateOpResult stateOpResult = (StateOpResult) result.getResult();
      if (stateOpResult.getItems().isEmpty()) {
        System.out.println("");
        System.out.println(">>> The operation has finished with empty result.");
        System.out.println("");
      } else {
        StateCommandHelper.printDbTable(stateOpResult.getItems());
        StateCommandHelper.printCodeBaseTable(stateOpResult.getItems());
        StateCommandHelper.printCompareTable(stateOpResult.getItems());
      }
    }
  }
}
