package com.ljc.apkdown.utils.ftpPool;

import lombok.Data;

@Data
public class FTPConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String encoding;
    private String workPath;
    private int maxTotal;
    private int maxWaitMillis;
}
