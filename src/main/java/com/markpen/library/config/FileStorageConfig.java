package com.markpen.library.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "library.file")
public class FileStorageConfig {
    private String filePath;
}
