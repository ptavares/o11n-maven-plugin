package com.github.ptavares.o11n.rest;

import com.github.ptavares.o11n.enums.FileBundle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * RestRequest
 *
 * @author Patrick Tavares
 */
public class RestRequest {

    private String resource;
    private Method method;
    private Authentication authentication;
    private Map<String, String> queryParams = new HashMap<>();
    private PluginFileInfo pluginFileInfo;

    public void setHttpAuthentication(String user, String password) {
        this.authentication = new Authentication(user, password);
    }

    public void addQueryParam(String option, String label) {
        queryParams.put(option, label);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }


    public void addPluginFile(File file, FileBundle bundle, boolean overwrite) {
        this.pluginFileInfo = new PluginFileInfo(file, bundle, overwrite);
    }

    public PluginFileInfo getPluginFileInfo() {
        return pluginFileInfo;
    }

    @Override
    public String toString() {
        return "RestRequest{" +
                "resource='" + resource + '\'' +
                ", method=" + method +
                ", authentication=" + authentication +
                ", queryParams=" + queryParams +
                ", pluginFileInfo=" + pluginFileInfo +
                '}';
    }

    /**
     * An http verb (those supported).
     */
    public enum Method {
        Get, Post, Delete
    }
}
