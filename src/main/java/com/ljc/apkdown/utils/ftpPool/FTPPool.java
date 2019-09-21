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

    private static String FTPHOST;


    private static int PORT;
    @Value("${ftp.username}")
    private static String USERNAME;
    @Value("${ftp.password}")
    private static String PASSWORD;
    @Value("${ftp.encoding}")
    private static String ENCODING;
    @Value("${ftp.maxTotal}")
    private static int MAXTOTAL;
    @Value("${ftp.maxWaitMillis}")
    private static int MAXWAITMILLIS;

    @Value("${ftp.host}")
    public void setHost(String host) {
        FTPHOST = host;
    }

    @Value("${ftp.port}")
    public void setPort(int port){
        PORT=port;
    }



    static {
        FTPConfig config = new FTPConfig();
        config.setEncoding("UTF-8");
        config.setHost("10.10.10.11"); //"10.10.10.11"
        config.setUsername("jenkins");
        config.setPassword("wm2012dx");
        config.setPort(21);
        config.setMaxTotal(8);
        config.setMaxWaitMillis(300_00);

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(10);// 不设置的话默认是8
        poolConfig.setMaxWaitMillis(30000);// 不设置默认无限等待

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
