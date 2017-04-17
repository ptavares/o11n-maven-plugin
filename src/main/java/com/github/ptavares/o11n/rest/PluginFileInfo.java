package com.github.ptavares.o11n.rest;

import com.github.ptavares.o11n.enums.FileBundle;

import java.io.File;

/**
 * Store plugin file information
 *
 * @author Patrick Tavares
 */
public class PluginFileInfo {

    /**
     * The plugin file
     */
    private final File pluginFile;

    /**
     * The plugin bundle type
     */
    private final FileBundle fileBundle;

    /**
     * boolean to know overwrite status
     */
    private final boolean overwrite;

    /**
     * Default constructor
     *
     * @param pluginFile Plugin file
     * @param fileBundle Plugin bundle type
     * @param overwrite  Overwrite plugin in vRO
     */
    public PluginFileInfo(File pluginFile, FileBundle fileBundle, boolean overwrite) {
        this.pluginFile = pluginFile;
        this.fileBundle = fileBundle;
        this.overwrite = overwrite;
    }

    /**
     * Getter for <code>pluginFile</code>
     *
     * @return Plugin file
     */
    public File getPluginFile() {
        return pluginFile;
    }

    /**
     * Getter for <code>fileBundle</code>
     *
     * @return {@link FileBundle} type
     */
    public FileBundle getFileBundle() {
        return fileBundle;
    }

    /**
     * Getter for <code>overwrite</code>
     *
     * @return boolean to know overwrite status
     */
    public boolean isOverwrite() {
        return overwrite;
    }


    @Override
    public String toString() {
        return "PluginFileInfo{" +
                "pluginFile=" + pluginFile +
                ", fileBundle=" + fileBundle +
                ", overwrite=" + overwrite +
                '}';
    }
}
