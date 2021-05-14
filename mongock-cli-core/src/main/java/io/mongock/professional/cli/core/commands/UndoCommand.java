package io.mongock.professional.cli.core.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "undo")
public class UndoCommand implements Callable<Integer> {

    @Option(names = {"-c", "--changeSetId"}, required = true)
    private String changeSetId;


    @Override
    public Integer call() throws Exception {
        System.out.println(String.format("This command runs the undo with changeSetId[%s]", changeSetId));
        return 0;
    }
}
