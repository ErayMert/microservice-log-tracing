package com.demo.customer.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String address;
    private Topic topic;

    @Getter
    @Setter
    public static class Topic {

        private String order;
    }
}
