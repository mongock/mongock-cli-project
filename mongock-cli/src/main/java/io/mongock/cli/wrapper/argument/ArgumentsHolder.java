package io.mongock.cli.wrapper.argument;

import io.mongock.cli.util.logger.CliLogger;
import io.mongock.cli.util.logger.CliLoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgumentsHolder {

    private static final CliLogger logger = CliLoggerFactory.getLogger(ArgumentsHolder.class);

    private final String[] args;

    public ArgumentsHolder(String[] args) {
        this.args = args;
    }

    public String[] getCleanArgs() {

        StringBuilder sb = new StringBuilder("cleaning arguments: ");
        Set<String> paramNamesSet = Arrays
                .stream(Argument.values())
                .map(arg -> Arrays.asList(arg.getLongName(), arg.getShortName()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet())
                .stream()
                .peek(arg -> sb.append(arg).append(" "))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        logger.debug(sb.toString());


        List<String> tempNewArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if (!paramNamesSet.contains(args[i].toLowerCase())) {
                tempNewArgs.add(args[i]);
            } else {
                i++;
            }
        }

        String[] newArgs = new String[tempNewArgs.size()];
        tempNewArgs.toArray(newArgs);
        logger.debug("cleaned args size: " + newArgs.length);
        StringBuilder sb2 = new StringBuilder("cleaned args: ");
        Stream.of(newArgs).forEach(arg -> sb2.append(arg).append(" "));
        logger.debug(sb2.toString());
        return newArgs;
    }

    public String getOrNull(Argument argument) {
        return getByArgument(argument, false);
    }

    public String getOrException(Argument argument) {
        return getByArgument(argument, true);
    }

    public Optional<String> getOptional(Argument argument) {
        return Optional.ofNullable(getByArgument(argument, false));
    }

    private String getByArgument(Argument argument, boolean throwException) {
        String value;
        if (((value = getValue(argument.getShortName())) == null) && ((value = getValue(argument.getLongName())) == null) && throwException) {
            String argumentName = argument.getLongName() + " or " + argument.getShortName();
            throw new RuntimeException(
                    String.format("Found [%s] flag with missing value. Please follow the format \"%s value\"", argumentName, argumentName)
            );
        }
        return value;
    }

    private String getValue(String paramName) {
        int i = 0;
        do {
            if (paramName.equalsIgnoreCase(args[i])) {
                if (args.length == i + 1) {
                    return null;
                }
                return args[i + 1];
            }
        } while ((++i) < args.length);
        return null;
    }

}
