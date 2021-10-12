package io.mongock.cli.core.commands;

import io.mongock.runner.core.executor.operation.Operation;


/**
 * This class is used to avoid using professional classes when the professional library is not imported,
 * so ClassNotFoundException is not thrown
 */
public final class ProfessionalOperationProxy {
	private ProfessionalOperationProxy(){
	}

	public static Operation undoOp(String changeId) {
		return new io.mongock.professional.runner.common.executor.operation.undo.UndoOp(changeId);
	}


}
