package io.mongock.cli.core.commands;

import io.mongock.runner.core.executor.operation.Operation;

/**
 * This class is used to avoid ClassNotFoundException when professional library is not imported
 */
public final class ProfessionalOperationProxy {

  private ProfessionalOperationProxy() {
  }
  
  public static Operation migrateUpToChangeOperation(String changeId) {
    return new io.mongock.professional.runner.common.executor.operation.migrate.MigrateUpToChangeOperation(changeId);
  }
  
  public static Operation undoAllOperation() {
    return new io.mongock.professional.runner.common.executor.operation.undo.UndoAllOperation();
  }

  public static Operation undoUpToChangeOperation(String changeId) {
    return new io.mongock.professional.runner.common.executor.operation.undo.UndoUpToChangeOperation(changeId);
  }

  public static Operation stateOperation() {
    return new io.mongock.professional.runner.common.executor.operation.state.StateOperation();
  }

}
