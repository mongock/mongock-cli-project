package io.mongock.professional.cli.springboot.config;

import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class MongockBanner implements org.springframework.boot.Banner {
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

        out.println("  __  __                                          _    \n" +
                " |  \\/  |                                        | |   \n" +
                " | \\  / |   ___    _ __     __ _    ___     ___  | | __\n" +
                " | |\\/| |  / _ \\  | '_ \\   / _` |  / _ \\   / __| | |/ /\n" +
                " | |  | | | (_) | | | | | | (_| | | (_) | | (__  |   < \n" +
                " |_|  |_|  \\___/  |_| |_|  \\__, |  \\___/   \\___| |_|\\_\\\n" +
                "                            __/ |                      \n" +
                "                           |___/                       ");
    }
}
