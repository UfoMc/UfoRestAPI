package de.matga;

import de.matga.api.BootStrap;

public class Main {

    public static void main(String[] args) {
        BootStrap bootStrap = new BootStrap();
        Runtime.getRuntime().addShutdownHook(new Thread(bootStrap::shutdown));
    }
}
