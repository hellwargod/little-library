package com.markpen.library.constant;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "library.file")
public class FileStorageConstant {
    private String uploadDir;

    private String commentDir;
}
