package io.mongock.professional.cli.springboot.config;

import org.springframework.boot.Banner;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class MongockBanner implements org.springframework.boot.Banner {
    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {


        out.println("" +
                "                                                           \n" +
                "            ((/                                             \n" +
                "            ./    *((((//**,,,,.                            \n" +
                "              /                     /(.                     \n" +
                "                //                      (           *       \n" +
                "                 /. ,(((                  ((/,  ,///////    \n" +
                "                /              (,   .(    (     /(((/////   \n" +
                "        /  (/  /.             /  ,((. (   (        (//////  \n" +
                "       (      ,(.             /,  .   (   (         ((((((  \n" +
                "      /*****      //             /(/      /*        *(((((  \n" +
                "      /              .(,                   ( ((((((((((((   \n" +
                "      *,                 /(               /.  *(((((((((    \n" +
                "  ,////(                    .           (       /((((       \n" +
                "   (    (.                              /                   \n" +
                "   /      /                            ,*                   \n" +
                "    (       *(.                       ,/                    \n" +
                "     */          */(((/((((          (                      \n" +
                "       //                          /,                       \n" +
                "          //                    //                          \n" +
                "              ///,        */(*                              \n" +
                "                                                            \n" +
                "                                                            " +
                "");




//        out.println("\n" +
//                "    __  ___                             __  \n" +
//                "   /  |/  /___  ____  ____ _____  _____/ /__\n" +
//                "  / /|_/ / __ \\/ __ \\/ __ `/ __ \\/ ___/ //_/\n" +
//                " / /  / / /_/ / / / / /_/ / /_/ / /__/ ,<   \n" +
//                "/_/  /_/\\____/_/ /_/\\__, /\\____/\\___/_/|_|  \n" +
//                "                   /____/                   ");
    }
}
