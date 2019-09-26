package com.ljc.apkdown.utils.ftpPool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("ftp")
@Component
@Data
public class FtpConfig {
    private boolean enabled = true;
    private String host;
    private int port = 21;
    private String userName;
    private String password;
    private boolean passiveMode = false;
    private String encoding = "UTF-8";
    private int connectTimeout = 30000;
    private int bufferSize = 8096;
    private int transferFileType;
}