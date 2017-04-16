package com.github.ptavares.o11n;

import com.github.ptavares.o11n.rest.RestClient;
import com.github.ptavares.o11n.rest.RestRequest;
import com.github.ptavares.o11n.rest.RestResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Mojo which deploys a created vRO plug-in to the configured vRO Server.
 * This Mojo must be configured within "o11nplugin-PLUGINNAME/pom.xml" Maven module.
 *
 * @author Patrick Tavares
 */
@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INSTALL)
public class DeployMojo extends AbstractO11nMojo {

    /**
     * RestClient to use for WebServices calls
     */
    private RestClient restClient;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // first check all params
        checkParams();
        // Log params
        if (getLog().isDebugEnabled()) {
            getLog().debug("Starting o11n-deploy with params :");
            getLog().debug(" - serverHost : " + getServerHost());
            getLog().debug(" - servicePort : " + getServicePort());
            getLog().debug(" - configPort : " + getConfigPort());
            getLog().debug(" - serviceUser : " + getServiceUser());
            getLog().debug(" - servicePassword : " + getServicePassword());
            getLog().debug(" - configUser : " + getConfigUser());
            getLog().debug(" - configPassword : " + getConfigPassword());
            getLog().debug(" - fileBundle : " + getFileBundle());
            getLog().debug(" - overwrite : " + isOverwrite());
            getLog().debug(" - restartService : " + isRestartService());
            getLog().debug(" - waitForRestart : " + isWaitForRestart());
            getLog().debug(" - deletePackage : " + isDeletePackage());
            getLog().debug(" - packageName : " + getPackageName());
            getLog().debug(" - deletePackageStrategy : " + getDeletePackageStrategy());
            getLog().debug(" - fileDirectoryPath : " + getFileDirectoryPath());
            getLog().debug(" - fileName : " + getFileName());
        }

        // First of all, check if file exist
        Path pluginFile = Paths.get(getFileDirectoryPath() + File.separator + getFileName() + getBundle().getFileSuffix());

        if (Files.exists(pluginFile) && Files.isRegularFile(pluginFile)) {
            // Init RestClient
            this.restClient = new RestClient(getLog());
            this.restClient.configureBaseUrl(getServerHost(), getServicePort(), getConfigPort());

            // 1. Delete package if is enable
            if (isDeletePackage() && uninstallPackage()) {
                getLog().info("Successfully delete package plugin");
            } else {
                logAndThrowFailureException(String.format("Failed to delete package '%s'", getPackageName()));
            }
            //2. Upload plugin
            if (installPlugin(pluginFile)) {
                getLog().info(String.format("Successfully install plugin '%s'", getFileName() + getBundle().getFileSuffix()));
                //3. Wait for restart
                if (isRestartService() && restartService()) {
                    getLog().info("Successfully restart requested vRO service");
                    if (isWaitForRestart() && waitForRestart()) {
                        getLog().info("Successfully restart vRO service");
                    } else {
                        logAndThrowFailureException("Failed to restart vRO service");
                    }
                } else {
                    logAndThrowFailureException("Failed to request restart vRO service");
                }
            } else {
                logAndThrowFailureException(String.format("Failed to install plugin '%s'", getFileName() + getBundle().getFileSuffix()));
            }
        } else {
            // No plugin file find
            throw new MojoFailureException("Plugin file '" + pluginFile + "' not found.");
        }

    }

    /**
     * Uninstall plugin package
     *
     * @return <code>true</code> if success, <code>false</code> otherwise
     */
    private boolean uninstallPackage() throws MojoFailureException {

        getLog().info("----------------------------");
        getLog().info("- Delete package requested - ");
        getLog().info("----------------------------");

        getLog().info(String.format("Deleting plug-in package '%s'...", getPackageName()));

        // Prepare Request
        RestRequest restRequest = new RestRequest();
        restRequest.setHttpAuthentication(getServiceUser(), getServicePassword());
        // Need to set package name with tailing dot (.) character
        restRequest.setResource("/packages/" + getPackageName() + ".");
        restRequest.addQueryParam("option", getDeleteStrategy().getLabel());
        restRequest.setMethod(RestRequest.Method.Delete);
        // Execute Request
        RestResponse response = this.restClient.executeServiceRequest(restRequest);
        // Analyse status code
        switch (response.getStatusCode()) {
            case 200:
                getLog().debug("HTTP 200. Package deleted successfully.");
                return true;
            case 204:
                getLog().debug("HTTP 204. No package found in vRO Server");
                return true;
            case 401:
                getLog().warn("HTTP 401. Authentication is required to delete a package from vRO Server.");
                return false;
            case 403:
                getLog().warn("HTTP 403. The provided user is not authorized to delete a package from vRO Server.");
                return false;
            case 404:
                getLog().warn("HTTP 404. Package not found on vRO Server. Skipping package deletion.");
                return true;
            default:
                getLog().warn("Unknown status code HTTP " + response.getStatusCode() + " returned from vRO Server. Please check if the package has been deleted. Got no idea !");
                return false;
        }
    }

    /**
     * Install plugin file
     *
     * @param pluginFile Path to plugin file
     * @return <code>true</code> if success, <code>false</code> otherwise
     */
    private boolean installPlugin(Path pluginFile) throws MojoFailureException {

        getLog().info("------------------");
        getLog().info("- Install Plugin -");
        getLog().info("------------------");

        getLog().info(String.format("Installing plugin file '%s'...", getFileName() + getBundle().getFileSuffix()));

        // Prepare Request
        RestRequest restRequest = new RestRequest();
        restRequest.setHttpAuthentication(getServiceUser(), getServicePassword());
        restRequest.setResource("/plugins/");
        restRequest.setMethod(RestRequest.Method.Post);
        restRequest.addPluginFile(pluginFile.toFile(), getBundle(), isOverwrite());
        // Execute Request
        RestResponse response = this.restClient.executeServiceRequest(restRequest);
        // Analyse status code
        switch (response.getStatusCode()) {
            case 201:
                getLog().debug("HTTP 201. Plugin successfully installed in vRO Server.");
                return true;
            case 204:
                getLog().debug("HTTP 204. Plugin successfully installed in vRO Server.");
                return true;
            case 401:
                getLog().warn("HTTP 401. Authentication is required to upload a plugin into vRO Server.");
                return false;
            case 403:
                getLog().warn("HTTP 403. The provided user is not authorized to upload a plugin into vRO Server.");
                return false;
            case 404:
                getLog().warn("HTTP 404. Requested resource not found. Please check vRO Server URL configuration and ensure that vRO Server is reachable from the machine running this Maven Mojo.");
                return false;
            default:
                getLog().warn("Unknown status code HTTP " + response.getStatusCode() + " returned from vRO Server. Please check if the plugin has been uploaded. Got no idea !");
                return false;

        }
    }

    /**
     * Restart vRO Server
     *
     * @return <code>true</code> if success, <code>false</code> otherwise
     */
    private boolean restartService() throws MojoFailureException {

        getLog().info("-------------------");
        getLog().info("- Restart Service -");
        getLog().info("-------------------");

        getLog().info(String.format("Restarting vRO service on host '%s'...", getServerHost()));

        // Prepare Request
        RestRequest restRequest = new RestRequest();
        restRequest.setHttpAuthentication(getConfigUser(), getConfigPassword());
        restRequest.setResource("/server/status/restart");
        restRequest.setMethod(RestRequest.Method.Post);

        // Execute Request
        RestResponse response = this.restClient.executeConfigRequest(restRequest);
        // Extract current status here.
        String currentStatus = response.getResponseBody();
        // Analyse status code
        switch (response.getStatusCode()) {
            case 200:
            case 201:
                getLog().debug(String.format("vRO service status : %s", currentStatus));
                return true;
            case 401:
                getLog().warn("HTTP 401. Authentication is required to restart the vRO service.");
                return false;
            case 403:
                getLog().warn("HTTP 403. The provided user is not authorized to restart the vRO service.");
                return false;
            case 404:
                getLog().warn("HTTP 404. The requested resource was not found. Please check vRO Server URL configuration and ensure that vRO Server is reachable from the machine running this Maven Mojo.");
                return false;
            default:
                getLog().warn("Unknown status code HTTP " + response.getStatusCode() + " returned from vRO Server. Please verify if the vRO service has been restarted. Got no idea !");
                return false;
        }
    }

    /**
     * Wait vRO Server
     *
     * @return <code>true</code> if success, <code>false</code> otherwise
     */
    private boolean waitForRestart() {

        getLog().info("----------------------------");
        getLog().info("- Wait for restart Service -");
        getLog().info("----------------------------");

        getLog().info(String.format("Waiting fot restart of vRO service on host '%s'...", getServerHost()));

        for (int i = 0; i < 10; ++i) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getLog().warn("Timeout. Unable to get the vRO configuration server. Please check your vRO server.");

        return true;
    }

}
