package com.github.ptavares.o11n.enums;

/**
 * Enum for delete package strategy manage by o11n-plugin.
 *
 * @author Patrick Tavares
 */
public enum PackageDeleteStrategy {

    DELETE_PACKAGE("deletePackage"),
    DELETE_PACKAGE_WITH_CONTENT("deletePackageWithContent"),
    DELETE_PACKAGE_KEEPING_SHARED("deletePackageKeepingShared");

    private String label;

    PackageDeleteStrategy(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
