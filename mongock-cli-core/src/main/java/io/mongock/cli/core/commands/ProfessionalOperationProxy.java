package io.mongock.cli.core.commands;

import io.mongock.runner.core.executor.operation.Operation;

/**
 * This class is used to avoid ClassNotFoundException when professional library is not imported
 */
public final class ProfessionalOperationProxy {

  private ProfessionalOperationProxy() {
  }

  public static Operation undoOp(String changeId) {
    return new io.mongock.professional.runner.common.executor.operation.undo.UndoOp(changeId);
  }

  public static Operation stateOp() {
    return new io.mongock.professional.runner.common.executor.operation.state.StateOp();
  }

}
