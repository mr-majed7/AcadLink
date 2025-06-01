package com.majed.acadlink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for storage settings.
 * This class holds configurable storage paths and settings.
 */
@Configuration
@ConfigurationProperties(prefix = "acadlink.storage")
@Getter
@Setter
public class StorageConfig {
    /**
     * Configuration for material storage.
     */
    private Materials materials = new Materials();

    @Getter
    @Setter
    public static class Materials {
        /**
         * Base path where material files are stored.
         * This should be an absolute path to a directory with write permissions.
         */
        private String path;
    }
} 