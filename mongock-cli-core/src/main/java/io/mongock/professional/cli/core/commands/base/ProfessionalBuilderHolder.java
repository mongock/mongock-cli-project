package io.mongock.professional.cli.core.commands.base;

import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.project.runner.common.ProfessionalRunnerBuilder;

public class ProfessionalBuilderHolder {

    private final ProfessionalRunnerBuilder builder;

    public ProfessionalBuilderHolder(RunnerBuilder builder) {
        this.builder = (ProfessionalRunnerBuilder)builder;
    }

    public ProfessionalRunnerBuilder getBuilder() {
        return builder;
    }
}
