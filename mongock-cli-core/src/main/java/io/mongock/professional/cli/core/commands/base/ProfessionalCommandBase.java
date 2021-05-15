package io.mongock.professional.cli.core.commands.base;

import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;

import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode;

public abstract class ProfessionalCommandBase implements Callable<Integer> {
    private final RunnerBuilder builder;

    public ProfessionalCommandBase(RunnerBuilder builder) {
        this.builder = builder;
    }


    @Override
    public final Integer call() {
        return checkProfessionalBuilder(builder) ? call(new ProfessionalBuilderHolder(builder)) : ExitCode.USAGE;
    }

    protected abstract Integer call(ProfessionalBuilderHolder builder);
    private static boolean checkProfessionalBuilder(RunnerBuilder builder) {
        try {
            Class.forName("io.mongock.project.runner.common.ProfessionalRunnerBuilder;");
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Professional feature not available in community distribution");
            //TODO LOG ERROR MESSAGE AND HOW TO FIX: Professional dependency
            return false;
        }
    }
}
