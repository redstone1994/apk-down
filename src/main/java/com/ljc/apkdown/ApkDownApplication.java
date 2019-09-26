package com.ljc.apkdown;

import com.ljc.apkdown.utils.ftpPool.FtpPoolConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class ApkDownApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApkDownApplication.class, args);
    }

}
