package com.github.ptavares.o11n.rest;

import com.github.ptavares.o11n.enums.FileBundle;

import java.io.File;

/**
 * Created by Patrick on 15/04/2017.
 */
public class PluginFileInfo {

    private File pluginFile;
    private FileBundle fileBundle;
    private boolean overwrite;

    public PluginFileInfo(File pluginFile, FileBundle fileBundle, boolean overwrite) {
        this.pluginFile = pluginFile;
        this.fileBundle = fileBundle;
        this.overwrite = overwrite;
    }

    public File getPluginFile() {
        return pluginFile;
    }

    public void setPluginFile(File pluginFile) {
        this.pluginFile = pluginFile;
    }

    public FileBundle getFileBundle() {
        return fileBundle;
    }

    public void setFileBundle(FileBundle fileBundle) {
        this.fileBundle = fileBundle;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
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
