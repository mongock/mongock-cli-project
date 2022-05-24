package io.mongock.cli.wrapper.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public enum Arguments {


    APP_JAR("-aj", "--app-jar"),
    DRIVER("-d", "--driver"),
    CLI_VERSION("--cli-version"),
    COMMUNITY_VERSION("--mongock-community-version"),
    PROFESSIONAL_VERSION("--mongock-professional-version"),
    LOG_LEVEL("--log-level");

    private String shortName;
    private String longName;

    Arguments(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    Arguments(String longName) {
        this.shortName = null;
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

    public Collection<String> getNames() {
        if(shortName != null) {
            return Arrays.asList(shortName, longName);
        } else {
            return Collections.singletonList(getDefaultName());
        }
    }
}
