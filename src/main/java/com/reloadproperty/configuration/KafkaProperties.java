package com.reloadproperty.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("kafkaconfiguration")
public class KafkaProperties {

    private String bootstrapServers;
    private String groupId;
    private String autoOffsetReset;
    private int sessionTimeoutMs;
    private Boolean enableAutoCommit;
    private int autoCommitIntervalMs;
    private int maxPollRecords;
    private int concurrency;
    private int pollTimeout;
    private String truststore;

}
