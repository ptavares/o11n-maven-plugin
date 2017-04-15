package com.github.ptavares.o11n.rest;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Simple rest client to execute rest call to vRO Server API
 *
 * @author Patrick Tavares
 */
public class RestClient {

    /**
     * Plugin {@link Log}
     */
    private final Log log;
    /**
     * ServiceBase URL
     */
    private String serviceBaseURL;
    /**
     * ConfigBase URL
     */
    private String configBaseURL;

    /**
     * Constructor
     *
     * @param log Plugin {@link Log}
     */
    public RestClient(Log log) {
        this.log = log;
    }

    /**
     * Configure base url for calling vRO's different API
     *
     * @param serverHost  vRO Server Host or IP
     * @param servicePort vRO Plugin Service REST API Port
     * @param configPort  vRO Config Service REST API Port
     */
    public void configureBaseUrl(String serverHost, Integer servicePort, Integer configPort) {
        this.serviceBaseURL = "https://" + serverHost + ":" + servicePort.toString() + "/vco/api";
        this.configBaseURL = "https://" + serverHost + ":" + configPort.toString() + "/vco-controlcenter/api";
    }

    /**
     * Execute a rest request to vRO Plugin Service REST API.
     *
     * @param request the request to be executed
     * @return the response of the rest request
     * @throws MojoFailureException In case of error
     */
    public RestResponse executeServiceRequest(RestRequest request) throws MojoFailureException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("start - executeServiceRequest(RestRequest request = %s)", request));
        }

        RestResponse restResponse = execute(request, this.serviceBaseURL);

        if (log.isDebugEnabled()) {
            log.debug(String.format("end - executeServiceRequest(RestRequest request) - restResponse = %s", restResponse));
        }
        return restResponse;
    }

    /**
     * Execute a rest request to vRO Config Service REST API.
     *
     * @param request the request to be executed
     * @return the response of the rest request
     * @throws MojoFailureException In case of error
     */
    public RestResponse executeConfigRequest(RestRequest request) throws MojoFailureException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("start - executeConfigRequest(RestRequest request = %s)", request));
        }

        RestResponse restResponse = execute(request, this.configBaseURL);

        if (log.isDebugEnabled()) {
            log.debug(String.format("end - executeConfigRequest(RestRequest request) - restResponse = %s", restResponse));
        }
        return restResponse;
    }


    /**
     * Execute a rest request.
     *
     * @param request the request to be executed
     * @param baseURL baseURL to use for API
     * @return the response of the rest request
     * @throws MojoFailureException In case of error
     */
    private RestResponse execute(RestRequest request, String baseURL) throws MojoFailureException {
        if (log.isDebugEnabled()) {
            log.debug(String.format("start - execute(RestRequest request = %s, String baseURL = %s)", new Object[]{request, baseURL}));
        }

        RestResponse restResponse = new RestResponse();
        try {
            // Init client with BasicAuth
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials(
                    request.getAuthentication().getUsername(),
                    request.getAuthentication().getPassword());
            provider.setCredentials(AuthScope.ANY, credentials);

            HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
            HttpRequestBase requestBase;

            if (request.getMethod() == RestRequest.Method.Get) {
                requestBase = new HttpGet(baseURL + request.getResource());
            } else if (request.getMethod() == RestRequest.Method.Delete) {
                requestBase = new HttpDelete(baseURL + request.getResource());
            } else {
                requestBase = new HttpPost(baseURL + request.getResource());
                if (request.getPluginFileInfo() != null) {
                    PluginFileInfo fileInfo = request.getPluginFileInfo();
                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.addBinaryBody("file", fileInfo.getPluginFile(),
                            ContentType.APPLICATION_OCTET_STREAM, fileInfo.getPluginFile().getName());
                    StringBody format = new StringBody(fileInfo.getFileBundle().name(), ContentType.MULTIPART_FORM_DATA);
                    StringBody overwrite = new StringBody(String.valueOf(fileInfo.isOverwrite()), ContentType.MULTIPART_FORM_DATA);
                    builder.addPart("format", format);
                    builder.addPart("overwrite", overwrite);
                    HttpEntity entity = builder.build();
                    ((HttpEntityEnclosingRequestBase) requestBase).setEntity(entity);
                }
            }

            if (!request.getQueryParams().isEmpty()) {
                URIBuilder builder = new URIBuilder(requestBase.getURI());
                for (Map.Entry<String, String> entry : request.getQueryParams().entrySet()) {
                    builder.addParameter(entry.getKey(), entry.getValue());
                }
                requestBase.setURI(builder.build());
            }

            requestBase.addHeader("content-type", ContentType.APPLICATION_JSON.getMimeType());
            requestBase.addHeader("accept", ContentType.APPLICATION_JSON.getMimeType());

            log.info("requestbase = " + requestBase);
            log.info("client = " + ReflectionToStringBuilder.toString(client));
            HttpResponse result = client.execute(requestBase);
            restResponse.setStatusCode(result.getStatusLine().getStatusCode());
            restResponse.setResponseBody(EntityUtils.toString(result.getEntity()));
        } catch (IOException | URISyntaxException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            String msgError = String.format("Error while calling vRO server API '%s'", baseURL + request.getResource());
            log.error(msgError, e);
            throw new MojoFailureException(msgError + " :\n" + sw.getBuffer().toString());
        }

        if (log.isDebugEnabled()) {
            log.debug(String.format("end - execute(RestRequest request, String baseURL) - restResponse = %s", new Object[]{restResponse}));
        }
        return restResponse;
    }


}
