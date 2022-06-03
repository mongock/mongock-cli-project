package io.mongock.cli.wrapper.argument;

import java.util.HashSet;
import java.util.Set;

public enum Argument {

    USER_APP_JAR("-aj", "--app-jar"),
    DRIVER("-d", "--driver"),
    CLI_VERSION("-cv", "--cli-version"),
    COMMUNITY_VERSION("-mcv", "--mongock-community-version"),
    PROFESSIONAL_VERSION("-mpv", "--mongock-professional-version"),
    LOG_LEVEL("-ll", "--log-level"),
    LICENSE_KEY("-lk", "--license-key");

    private final String shortName;
    private final String longName;

    Argument(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }


    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getDefaultName() {
        return getLongName();
    }

    public static void validateArguments() {
        Set<String> insertedValues = new HashSet<>();
        for(Argument arg: values()) {
            checkValueInserted(insertedValues, arg.getLongName());
            checkValueInserted(insertedValues, arg.getShortName());
            insertedValues.add(arg.getLongName());
            insertedValues.add(arg.getShortName());
        }
    }

    private static void checkValueInserted(Set<String> values, String value) {
        if(values.contains(value)) {
            throw new RuntimeException(String.format("Argument[%s] duplicated", value));
        }
    }
}
