package com.ljc.apkdown.utils.ftpPool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("ftp")
@Component
@Data
public class FtpConfig {
    private String host;
    private int port;
    private String username;
    private String password;
}