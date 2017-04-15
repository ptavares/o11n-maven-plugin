package com.github.ptavares.o11n.rest;

/**
 * @author Patrick Tavares
 */
public class RestResponse {

    /**
     * Response body for this response
     */
    private String responseBody;
    /**
     * Status code of this response
     */
    private Integer statusCode;

    /**
     * @return the status code of this response
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode the status code for this response
     * @return this response
     */
    public RestResponse setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * @return the response body for this response
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody the response body for this response
     * @return this response
     */
    public RestResponse setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    @Override
    public String toString() {
        return "RestResponse{" +
                "responseBody='" + responseBody + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
