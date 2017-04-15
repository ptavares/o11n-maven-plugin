package com.github.ptavares.o11n.enums;

/**
 * Enum for file bundle manage by o11n-plugin.
 *
 * @author Patrick Tavares
 */
public enum FileBundle {


    VMOAPP(".vmoapp"), DAR(".dar");


    private final String fileSuffix;

    FileBundle(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }
}
