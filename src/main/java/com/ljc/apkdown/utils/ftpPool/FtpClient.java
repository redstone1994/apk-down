package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * ftpclient 工厂
 *
 * @author
 */
@Slf4j
@Component
public class FtpClient {

    //本地编码集对象
    private static String encode = Charset.defaultCharset().toString();

    // FTP编码为iso-8859-1
    private static final String SERVER_CHARSET = "ISO-8859-1";

    //FTP下载时读入内存的大小
    private static final int BUFFER_SIZE = 1024000;

    private static String HOST;
    private static String PORT;
    private static String USERNAME;
    private static String PASSWORD;
    @Value("${ftp.host}")
    public void setHOST(String host){
        this.HOST=host;
    }
    @Value("${ftp.port}")
    public void setPORT(String port){
        this.PORT=port;
    }
    @Value("${ftp.username}")
    public void setUSERNAME(String username) {
        this.USERNAME=username;
    }
    @Value("${ftp.password}")
    public void setPASSWORD(String password){
        this.PASSWORD=password;
    }

    /**
     * 获取FTP连接对象，连接FTP成功返回FTP对象，
     * 连接FTP失败超过最大次数返回null,使用前请判断是否为空
     *
     * @return FTPClient FTP连接对象
     */
    public FTPClient getFTPClient() {
        //FTP连接对象
        FTPClient ftpClient = null;

        try {
            ftpClient = new FTPClient();
            //设置FTP服务器IP和端口
            ftpClient.connect(HOST, Integer.parseInt(PORT));
            //设置超时时间,毫秒
            ftpClient.setConnectTimeout(50000);
            //登录FTP
            ftpClient.login(USERNAME, PASSWORD);

            //设置被动传输模式
            ftpClient.enterLocalPassiveMode();
            //ftpClient.enterRemotePassiveMode();
            //二进制传输
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //设置读入内存文件大小
            ftpClient.setBufferSize(BUFFER_SIZE);

            //获取FTP连接状态码 ，大于等于200 小于300状态码表示连接正常
            int connectState = ftpClient.getReplyCode();

            //连接失败重试
            int reNum = 0;
            while (!FTPReply.isPositiveCompletion(connectState)
                    && reNum < 3) {
                ftpClient.disconnect();
                log.info("重试：{}",reNum);
                ++reNum;
                ftpClient.login(USERNAME, PASSWORD);

            }
            if (reNum < 3) {
                log.info("FTP连接成功:{}",USERNAME);
            } else {
                ftpClient = null;
                log.error("FTP连接失败");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ftpClient;
    }

    /**
     * 断开FTP
     *
     * @param ftpClient fpt连接对象
     */
    public void closeFTP(FTPClient ftpClient) {

        if (null != ftpClient) {
            try {
                //登出FTP
                ftpClient.disconnect();
                log.info("登出FTP成功");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                try {
                    //断开FTP
                    ftpClient.disconnect();
                    log.info("断开FTP成功");
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

}