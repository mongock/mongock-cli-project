package io.mongock.cli.wrapper.util;

import java.util.Locale;

public class DriverWrapperFactory {

    private final String jarsLib;
    private final String version;

    public DriverWrapperFactory(String jarLibs, String version) {
        this.jarsLib = jarLibs;
        this.version = version;
    }

    public String getJar(String driverInput) {
        return DriverWrapper
                .valueOf(driverInput.toUpperCase())
                .getJar(jarsLib, version);
    }



}

