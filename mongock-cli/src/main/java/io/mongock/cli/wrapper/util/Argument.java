package io.mongock.cli.wrapper.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum Argument {


    APP_JAR("-aj", "--app-jar"),
    DRIVER("-d", "--driver"),
    CLI_VERSION("-cv", "--cli-version"),
    COMMUNITY_VERSION("-mcv", "--mongock-community-version"),
    PROFESSIONAL_VERSION("-mpv", "--mongock-professional-version"),
    LOG_LEVEL("-ll", "--log-level");

    private String shortName;
    private String longName;

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

//    public Collection<String> getNames() {
//        if (shortName != null) {
//            return Arrays.asList(shortName, longName);
//        } else {
//            return Collections.singletonList(getDefaultName());
//        }
//    }
}
