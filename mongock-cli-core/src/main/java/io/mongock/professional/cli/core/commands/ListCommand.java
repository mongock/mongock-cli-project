package io.mongock.professional.cli.core.commands;

import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "undo")
public class ListCommand implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        System.out.println("This command lists all the changes with state and more information");
        return 0;
    }
}
