package com.get3bids.scrappers.cofig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("acculynx")
public class AccuLynxConfig {
    private String apiKey;
    private String baseURL;
}
