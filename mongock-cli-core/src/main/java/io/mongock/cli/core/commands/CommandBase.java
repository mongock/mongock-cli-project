package io.mongock.cli.core.commands;

import io.mongock.api.exception.MongockException;
import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;
import io.mongock.runner.core.builder.RunnerBuilder;
import java.util.concurrent.Callable;
import picocli.CommandLine;

import static io.mongock.runner.core.builder.BuilderType.PROFESSIONAL;

public abstract class CommandBase<T> implements Callable<T> {

    private static final CliLogger logger = CliLoggerFactory.getLogger(CommandBase.class);
  
    protected final RunnerBuilder builder;

    public CommandBase(RunnerBuilder builder) {
        this.builder = builder;
    }

    @Override
    public T call() throws Exception {
        if(builder == null) {
            throw new MongockException("Mongock builder needs to be provided for this command");
        }
        return execution();
    }
    
    public void printUsage() {
        CommandLine.usage(this, System.out);
    }

    public abstract T execution() throws Exception;
    
    protected boolean checkProfessionalBuilder() {
      if (builder.getType() != PROFESSIONAL) {
        logger.error("Operation not supported in community edition");
        CommandLine.usage(this, System.out);
        return false;
      }
      return true;
    }
}
