package edu.ezip.ing1.pds.commons;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    private String requestId;
    private Object responseBody;

    public Response() { }

    public Response(String requestId, Object responseBody) {
        this.requestId = requestId;
        this.responseBody = responseBody;
    }

    @JsonProperty("request_id")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("request_id")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("response_body")
    public Object getResponseBody() {
        return responseBody;
    }

    @JsonProperty("response_body")
    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return "Response{" +
                "requestId='" + requestId + '\'' +
                ", responseBody=" + responseBody +
                '}';
    }
}
