package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

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
    List<FTPFileBean> arFiles = new ArrayList<>();

    public List<FTPFileBean> list(String pathName, String ext, String item) throws IOException {
        client.enterLocalPassiveMode();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {

            FTPFileBean ffb = new FTPFileBean();
            String directory = pathName;
            //更换目录到当前目录
//            this.client.changeWorkingDirectory(directory);
            FTPFile[] files = this.client.listFiles(directory);
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(ext)) {
                        ffb.setFileName(file.getName());
                        ffb.setItem(item);
                        ffb.setFilePath(directory + file.getName());
                        arFiles.add(ffb);
                    }
                } else if (file.isDirectory()) {
                    list(directory + file.getName() + "/", ext, item);
                }
            }

            return arFiles;
        }
        return null;
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename) {
        client.enterLocalPassiveMode();
        try {
            log.info("开始下载文件");

            FTPFile[] ftpFiles = client.listFiles(pathname);
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {

                    client.retrieveFile(file.getName());
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
