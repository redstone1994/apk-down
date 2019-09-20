package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;
import java.net.SocketException;

@Slf4j
public class FTPPoolFactory implements PooledObjectFactory<FTPClient> {

    private FTPConfig ftpConfig;
    private static int BUFF_SIZE = 256000;

    public FTPPoolFactory(FTPConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    @Override
    public PooledObject<FTPClient> makeObject() throws Exception {
        FTPClient ftpClient = new FTPClient();
        ftpClient.setDefaultPort(ftpConfig.getPort());
        ftpClient.setConnectTimeout(300000);
        ftpClient.setDataTimeout(180000);
        ftpClient.setControlKeepAliveTimeout(60);
        ftpClient.setControlKeepAliveReplyTimeout(60);
        ftpClient.setControlEncoding(ftpConfig.getEncoding());

        FTPClientConfig clientConfig = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        clientConfig.setServerLanguageCode(ftpConfig.getEncoding());
        ftpClient.configure(clientConfig);

        try {
            ftpClient.connect(ftpConfig.getHost());
        } catch (SocketException exp) {
            log.warn("connect timeout with FTP server:" + ftpConfig.getHost());
            throw new Exception(exp.getMessage());
        } catch (IOException exp) {
            log.warn("connect FTP server:" + ftpConfig.getHost() + " meet error:" + exp.getMessage());
            throw new Exception(exp.getMessage());
        }

        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            log.error("FTPServer refused connection");
            return null;
        }
        boolean result = ftpClient.login(ftpConfig.getUsername(), ftpConfig.getPassword());
        if (!result) {
            log.warn("FTP server refused refused connection.");
            throw new Exception(
                    "login failed with FTP server:" + ftpConfig.getHost() + " may user name and password is wrong");
        }
        ftpClient.setBufferSize(BUFF_SIZE);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setFileTransferMode(FTP.COMPRESSED_TRANSFER_MODE);

        ftpClient.changeWorkingDirectory(ftpConfig.getWorkPath());

        return new DefaultPooledObject<FTPClient>(ftpClient);
    }

    @Override
    public void destroyObject(PooledObject<FTPClient> pooledObject) throws Exception {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not disconnect from server.", e);
        }
    }

    @Override
    public boolean validateObject(PooledObject<FTPClient> pooledObject) {
        FTPClient ftpClient = pooledObject.getObject();
        try {
            return ftpClient.sendNoOp();
        } catch (IOException e) {
            log.error("Failed to validate client:", e);
            return false;
        }
    }

    @Override
    public void activateObject(PooledObject<FTPClient> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<FTPClient> pooledObject) throws Exception {

    }
}
