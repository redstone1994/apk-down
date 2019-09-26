package com.ljc.apkdown.utils.ftpPool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(FTPHelper.class)
@EnableConfigurationProperties(FtpConfig.class)
public class FtpAutoConfigure {

    @Autowired
    private FtpConfig properties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "ftp", value = "enabled", havingValue = "true")
    public FTPHelper exampleService() throws Exception {
        return new FTPHelper(properties);
    }

}
