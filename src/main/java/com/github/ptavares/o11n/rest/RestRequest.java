package com.github.ptavares.o11n.rest;

import com.github.ptavares.o11n.enums.FileBundle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * RestRequest object
 *
 * @author Patrick Tavares
 */
public class RestRequest {

    /**
     * Resource URL to call
     */
    private String resource;

    /**
     * Http verb to use
     */
    private Method method;
    /**
     * Stored {@link Authentication} information
     */
    private Authentication authentication;

    /**
     * {@link Map} with query params
     */
    private Map<String, String> queryParams = new HashMap<>();

    /**
     * Stored {@link PluginFileInfo}
     */
    private PluginFileInfo pluginFileInfo;

    /**
     * Default constructor
     */
    public RestRequest() {
        super();
    }

    /**
     * Set authentication information for this {@link RestRequest}
     *
     * @param user     user name to use
     * @param password user password to use
     * @return the configured {@link RestRequest}
     */
    public RestRequest setHttpAuthentication(String user, String password) {
        this.authentication = new Authentication(user, password);
        return this;
    }

    /**
     * Add a query param for this {@link RestRequest}
     *
     * @param paramName  param name
     * @param paramValue param value
     * @return the configured {@link RestRequest}
     */
    public RestRequest addQueryParam(String paramName, String paramValue) {
        queryParams.put(paramName, paramValue);
        return this;
    }

    /**
     * Add plugin file information to upload.
     *
     * @param file      The file plugin
     * @param bundle    The plugin {@link FileBundle}
     * @param overwrite Overwrite plugin in vRO server
     * @return the configured {@link RestRequest}
     */
    public RestRequest addPluginFile(File file, FileBundle bundle, boolean overwrite) {
        this.pluginFileInfo = new PluginFileInfo(file, bundle, overwrite);
        return this;
    }

    /**
     * Getter for <code>resource</code> property.
     *
     * @return The URL resource to call
     */
    public String getResource() {
        return resource;
    }

    /**
     * Set the resource URL to call
     *
     * @param resource value for resource URL
     * @return the configured {@link RestRequest}
     */
    public RestRequest setResource(String resource) {
        this.resource = resource;
        return this;
    }

    /**
     * Getter for <code>method</code> property.
     *
     * @return The http verb to use of {@link com.github.ptavares.o11n.rest.RestRequest.Method}
     */
    public Method getMethod() {
        return method;
    }

    /**
     * The http verb to use of {@link com.github.ptavares.o11n.rest.RestRequest.Method}
     *
     * @param method {@link com.github.ptavares.o11n.rest.RestRequest.Method} to use
     * @return the configured {@link RestRequest}
     */
    public RestRequest setMethod(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Getter for <code>authentication</code> property.
     *
     * @return {@link Authentication} to use for this {@link RestRequest}
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * Getter for <code>queryParams</code> property.
     *
     * @return query params to use for this {@link RestRequest}
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }


    /**
     * Getter for <code>pluginFileInfo</code> property.
     *
     * @return {@link PluginFileInfo} to use for this {@link RestRequest}
     */
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
