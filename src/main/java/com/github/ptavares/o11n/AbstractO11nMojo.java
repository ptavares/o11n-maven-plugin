package com.github.ptavares.o11n;

import com.github.ptavares.o11n.enums.FileBundle;
import com.github.ptavares.o11n.enums.PackageDeleteStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.Arrays;

/**
 * Abstract class for all o11n-maven plugin.
 *
 * @author Patrick Tavares
 */
abstract class AbstractO11nMojo extends AbstractMojo {

    /**
     * Set MavenProject to get default values.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * vRO Server Host or IP
     */
    @Parameter(required = true, property = "o11nPlugin.serverHost", defaultValue = "localhost")
    private String serverHost;

    /**
     * vRO Plugin Service REST API Port, usually 8281.
     * Check <code>http://{vcoHost}:{port}/vco/api/docs</code> for API docs
     */
    @Parameter(required = false, property = "o11nPlugin.servicePort", defaultValue = "8281")
    private Integer servicePort;

    /**
     * vRO Config Service REST API Port, usually 8283.
     * Check <code>http://{vcoHost}:{port}/vco-controlcenter/api/docs</code> for API docs
     */
    @Parameter(required = false, property = "o11nPlugin.configPort", defaultValue = "8283")
    private Integer configPort;

    /**
     * Username of a user with enough permissions to import plugins into vRO.
     */
    @Parameter(required = true, property = "o11nPlugin.serviceUser", defaultValue = "vcoadmin")
    private String serviceUser;

    /**
     * Password for the <code>serviceUser</code> property.
     */
    @Parameter(required = true, property = "o11nPlugin.servicePassword", defaultValue = "vcoadmin")
    private String servicePassword;

    /**
     * Username of a user with enough permissions to restart the vRO service.
     */
    @Parameter(required = false, property = "o11nPlugin.configUser", defaultValue = "root")
    private String configUser;

    /**
     * Password for the <code>configUser</code> property.
     */
    @Parameter(required = false, property = "o11nPlugin.configPassword")
    private String configPassword;

    /**
     * Path to the plugin file directory that should be installed.
     * Default value : <code>${project.build.directory}</code>
     */
    @Parameter(required = false, property = "o11nPlugin.fileDirectoryPath", defaultValue = "${project.build.directory}")
    private String fileDirectoryPath;

    /**
     * The plugin filename of the plug-in that should be installed without any file extension.
     * The extension matches the <code>fileBundle</code> property.
     * Default value : <code>${project.build.finalName}</code>
     */
    @Parameter(required = false, property = "o11nPlugin.fileName", defaultValue = "${project.build.finalName}")
    private String fileName;

    /**
     * The plugin bundle format. Accepted values are :
     * <ul>
     * <li>DAR</li>
     * <li>VMOAPP</li>
     * </ul>
     */
    @Parameter(required = false, property = "o11nPlugin.fileBundle")
    private String fileBundle;

    /**
     * {@link FileBundle} for <code>fileBundle</code> property
     */
    private FileBundle bundle;

    /**
     * Property for force vRO to reinstall the plugin.
     * Default value : <code>false</code>
     */
    @Parameter(required = false, property = "o11nPlugin.overwrite", defaultValue = "false")
    private boolean overwrite;

    /**
     * Property for restart vRO service after install plugin.
     * Default value : <code>false</code>
     */
    @Parameter(required = false, property = "o11nPlugin.restartService", defaultValue = "false")
    private boolean restartService;

    /**
     * Property to wait for vRO's service restart.
     * Set to <code>true</code>, this option will make this Mojo wait up to 300 seconds max (timeout)
     * --------------------------------------------------------------------------------------------------
     * <b>Note</b>:
     * This option will only be ignored if <code>restartService</code> is set to <code>false</code>.
     * --------------------------------------------------------------------------------------------------
     * Default value : <code>false</code>
     */
    @Parameter(required = false, property = "o11nPlugin.waitForRestart", defaultValue = "false")
    private boolean waitForRestart;

    /**
     * Property to delete plugin package before installing the new one.
     * --------------------------------------------------------------------------------------------------
     * <b>Warning</b>:
     * All non synchronized package modifications will be lost (workflows, actions, ....)
     * --------------------------------------------------------------------------------------------------
     * Default value : <code>false</code>
     */
    @Parameter(required = false, property = "o11nPlugin.deletePackage", defaultValue = "false")
    private boolean deletePackage;

    /**
     * Set the delete strategy for package, possible delete options :
     * <ul>
     * <li>deletePackage : deletes the package without the content.</li>
     * <li>deletePackageWithContent : deletes the package along with the content.
     * If other packages share elements with this package, they will be deleted.</li>
     * <li>deletePackageKeepingShared : deletes the package along with the content.
     * If other packages share elements with this package, the elements will not be removed.</li>
     * </ul>
     * <p>
     * By default, the delete package strategy is deletePackageKeepingShared.
     */
    @Parameter(required = false, property = "o11nPlugin.deletePackageStrategy")
    private String deletePackageStrategy;

    /**
     * {@link PackageDeleteStrategy} for <code>deletePackageStrategy</code> property
     */
    private PackageDeleteStrategy deleteStrategy;

    /**
     * The package name to be delete if property <code>deletePackage</code> is set to <code>true</code>.
     * -------------------------------------------------------------------------------------------------
     * <b>Note :</b>
     * this package name is the package name specified in the <code>pkg-name</code>
     * attribute of the <tt>dunes-meta-inf.xml</tt> file.
     * -------------------------------------------------------------------------------------------------
     */
    @Parameter(required = false, property = "o11nPlugin.packageName")
    private String packageName;


    /**
     * Check all plugin params
     */
    protected void checkParams() throws MojoFailureException {
        if (StringUtils.isEmpty(this.fileDirectoryPath)) {
            fileDirectoryPath = project.getBuild().getOutputDirectory();
            getLog().debug(String.format("%s not specified, get default value : %s", new Object[]{"fileDirectoryPath", fileDirectoryPath}));
        }
        if (StringUtils.isEmpty(this.fileName)) {
            fileName = project.getBuild().getFinalName();
            getLog().debug(String.format("%s not specified, get default value : %s", new Object[]{"fileName", fileName}));
        }
        if (StringUtils.isEmpty(this.fileBundle)) {
            fileBundle = FileBundle.DAR.name();
            getLog().debug(String.format("%s not specified, get default value : %s", new Object[]{"fileBundle", fileBundle}));
        } else {
            try {
                bundle = FileBundle.valueOf(fileBundle.toUpperCase());
            } catch (IllegalArgumentException e) {
                logAndThrowFailureException(String.format("Error : 'fileBundle' unknown, authorized values are '%s'", Arrays.toString(FileBundle.values())));
            }
        }
        if (this.servicePort == null) {
            this.servicePort = 8281;
            getLog().debug(String.format("%s not specified, get default value : %s", new Object[]{"servicePort", servicePort}));
        }
        ckeckPort("servicePort", this.servicePort);
        if (this.configPort == null) {
            this.configPort = 8283;
            getLog().debug(String.format("%s not specified, get default value : %s", new Object[]{"configPort", configPort}));
        }
        ckeckPort("configPort", this.configPort);

        // Don't need to wait for pending changes if restart is not enable
        if (waitForRestart && !restartService) {
            waitForRestart = false;
        }
        // Need to check all login/password params
        if (restartService) {
            // Only check config user -> password is optional
            if (StringUtils.isEmpty(configUser)) {
                logAndThrowFailureException(String.format("Error : 'restartService' was enable but '%s' param was not defined", "configUser"));
            }
            // Only check service password -> default login : vcoadmin
            if (StringUtils.isEmpty(servicePassword)) {
                logAndThrowFailureException(String.format("Error : 'restartService' was enable but '%s' param was not defined", "servicePassword"));
            }
        }
        // No deletePackage is possible if a packageName is not defined
        if (deletePackage && StringUtils.isEmpty(packageName)) {
            logAndThrowFailureException(String.format("Error : 'deletePackage' was enable but '%s' param was not defined", "packageName"));
        }
        if (deletePackage && !StringUtils.isEmpty(deletePackageStrategy)) {
            try {
                deleteStrategy = PackageDeleteStrategy.valueOf(deletePackageStrategy.toUpperCase());
            } catch (IllegalArgumentException e) {
                logAndThrowFailureException(String.format("Error : 'deletePackageStrategy' unknown, authorized values are '%s'", Arrays.toString(PackageDeleteStrategy.values())));
            }
        }
    }

    /**
     * Check port range
     *
     * @param param param name
     * @param port  port value
     * @throws MojoFailureException if port is out of range
     */
    private void ckeckPort(String param, int port) throws MojoFailureException {
        if (port < 1 || port > 65535) {
            logAndThrowFailureException(String.format("Error : '%s' is over port range [1-65535]", param));
        }
    }

    /**
     * Log and throw {@link MojoFailureException}
     *
     * @param msgError Message to log
     * @throws MojoFailureException Exception throwed with <code>msgError</code>
     */
    protected void logAndThrowFailureException(String msgError) throws MojoFailureException {
        getLog().error(msgError);
        throw new MojoFailureException(msgError);
    }

    public MavenProject getProject() {
        return project;
    }

    public String getServerHost() {
        return serverHost;
    }

    public Integer getServicePort() {
        return servicePort;
    }

    public Integer getConfigPort() {
        return configPort;
    }

    public String getServiceUser() {
        return serviceUser;
    }

    public String getServicePassword() {
        return servicePassword;
    }

    public String getConfigUser() {
        return configUser;
    }

    public String getConfigPassword() {
        return configPassword;
    }

    public String getFileDirectoryPath() {
        return fileDirectoryPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileBundle() {
        return fileBundle;
    }

    public FileBundle getBundle() {
        return bundle;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public boolean isRestartService() {
        return restartService;
    }

    public boolean isWaitForRestart() {
        return waitForRestart;
    }

    public boolean isDeletePackage() {
        return deletePackage;
    }

    public String getDeletePackageStrategy() {
        return deletePackageStrategy;
    }

    public PackageDeleteStrategy getDeleteStrategy() {
        return deleteStrategy;
    }

    public String getPackageName() {
        return packageName;
    }
}
