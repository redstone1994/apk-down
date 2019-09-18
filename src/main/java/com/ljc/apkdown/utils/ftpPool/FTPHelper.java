package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;
import sun.net.ftp.FtpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FTPHelper {

    FTPClient client = FTPPool.getFTPClient();

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public void ftpList(String pathName, List<String> arFiles) throws IOException {
        client.enterLocalPassiveMode();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            String directory = pathName;
            //更换目录到当前目录
            this.client.changeWorkingDirectory(directory);
            FTPFile[] files = this.client.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    arFiles.add(directory + file.getName());

                } else if (file.isDirectory()) {
                    ftpList(directory + file.getName() + "/", arFiles);
                }
            }
        }
    }

    /**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @param ext      文件的扩展名
     * @throws IOException
     */
    public void list(String pathName, String ext, List<String> arFiles) throws IOException {
        client.enterLocalPassiveMode();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            String directory = pathName;
            //更换目录到当前目录
            this.client.changeWorkingDirectory(directory);
            FTPFile[] files = this.client.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(ext)) {
                        arFiles.add(file.getName());
                    }
                } else if (file.isDirectory()) {
                    list(directory + file.getName() + "/", ext, arFiles);
                }
            }
        }
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename, String localpath) {
        client.enterLocalPassiveMode();
        boolean flag = false;
        OutputStream os = null;
        try {
            log.info("开始下载文件");
            //切换FTP目录
            client.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = client.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + file.getName());

                    os = new FileOutputStream(localFile);
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    flag = client.retrieveFile(file.getName(), os);
                    if (!flag) {
                        log.info("下载文件失败");
                    } else {
                        log.info("下载文件成功");
                    }
                    os.close();
                }
            }
        } catch (Exception e) {
            log.info("下载文件失败" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }



}
