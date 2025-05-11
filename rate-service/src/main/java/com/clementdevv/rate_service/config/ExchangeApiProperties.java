package com.clementdevv.rate_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchange-rate-api")
public class ExchangeApiProperties {
    private String url;
    private String key;

    // getters and setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getKey() {
        System.out.println(key);
        return key; }
    public void setKey(String key) { this.key = key; }
}
