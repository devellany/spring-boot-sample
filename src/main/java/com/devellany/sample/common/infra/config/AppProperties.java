package com.devellany.sample.common.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app")
public class AppProperties {

    private String host;

    private String title;

    private String version;

    private String helpEmail;

    private Integer tokenAvailablePeriod;

}
