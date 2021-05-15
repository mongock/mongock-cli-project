package io.mongock.professional.cli.core.commands;

import com.github.cloudyrock.mongock.runner.core.builder.RunnerBuilder;
import io.mongock.professional.cli.core.commands.base.ProfessionalBuilderHolder;
import io.mongock.professional.cli.core.commands.base.ProfessionalCommandBase;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "undo", description = "")
public class UndoCommand extends ProfessionalCommandBase {

    @Option(names = {"-c", "--changeSetId"}, required = true)
    private String changeSetId;

    public UndoCommand(RunnerBuilder builder) {
        super(builder);
    }

    @Override
    public Integer call(ProfessionalBuilderHolder builder) {
        System.out.println(String.format("This command runs the undo with changeSetId[%s]", changeSetId));
        builder.getBuilder().undo("change");
        return 0;
    }
}
