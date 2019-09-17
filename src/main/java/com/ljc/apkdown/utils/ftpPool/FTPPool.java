package com.ljc.apkdown.utils.ftpPool;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FTPPool {
    private static final GenericObjectPool<FTPClient> internalPool;

    @Value("${ftp.host}")
    private static String host;
    @Value("${ftp.port}")
    private static int port;
    @Value("${ftp.username}")
    private static String username;
    @Value("${ftp.password}")
    private static String password;
    @Value("${ftp.encoding}")
    private static String encoding;
    @Value("${ftp.maxTotal}")
    private static int maxTotal;
    @Value("${ftp.maxWaitMillis}")
    private static int maxWaitMillis;

    static {
        FTPConfig config = new FTPConfig();
        config.setEncoding(encoding);
        config.setHost(host);
        config.setUsername(username);
        config.setPassword(password);
        config.setPort(port);
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(maxWaitMillis);
        System.out.printf(String.valueOf(config));

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(config.getMaxTotal());// 不设置的话默认是8
        poolConfig.setMaxWaitMillis(config.getMaxWaitMillis());// 不设置默认无限等待

        internalPool = new GenericObjectPool<FTPClient>(new FTPPoolFactory(config), poolConfig);
    }

    /**
     * 获取资源
     *
     * @return
     */
    public static FTPClient getFTPClient() {
        try {
            return internalPool.borrowObject();
        } catch (Exception e) {
            log.error("获取FTP连接异常：", e);
            return null;
        }
    }

    /**
     * 归还资源
     *
     * @param ftpClient
     */
    public static void returnFTPClient(FTPClient ftpClient) {
        try {
            internalPool.returnObject(ftpClient);
        } catch (Exception e) {
            log.error("释放FTP连接异常：", e);
        }
    }

    /**
     * 销毁池子
     */
    public static void destroy() {
        try {
            internalPool.close();
        } catch (Exception e) {
            log.error("销毁FTP数据源异常：", e);
        }
    }
}
