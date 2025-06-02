package org.example.casdoorassignment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component 
@ConfigurationProperties(prefix = "openidconnect")
@Validated
public class CasdoorProperties { 

    private String endpoint;

    private String clientId;

    private String clientSecret;

    private String redirectUriBase = "https://localhost:7443"; 
    private String callbackPath = "/callback"; 

    private String defaultScopes = "openid profile email";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUriBase() {
        return redirectUriBase;
    }

    public void setRedirectUriBase(String redirectUriBase) {
        this.redirectUriBase = redirectUriBase;
    }

    public String getCallbackPath() {
        return callbackPath;
    }

    public void setCallbackPath(String callbackPath) {
        this.callbackPath = callbackPath;
    }

    public String getDefaultScopes() {
        return defaultScopes;
    }

    public void setDefaultScopes(String defaultScopes) {
        this.defaultScopes = defaultScopes;
    }

    public String getFullRedirectUri() {
        return redirectUriBase + callbackPath;
    }
}

