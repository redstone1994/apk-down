package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Component
public class FTPHelper {

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public void ftpList(FTPClient client,String pathName, List<String> arFiles) throws IOException {

        try {
            if (pathName.startsWith("/") && pathName.endsWith("/")) {
                String directory = pathName;
                //更换目录到当前目录
                client.changeWorkingDirectory(directory);
                FTPFile[] files = client.listFiles();
                for (FTPFile file : files) {
                    if (file.isFile()) {
                        arFiles.add(directory + file.getName());

                    } else if (file.isDirectory()) {
                        ftpList(client,directory + file.getName() + "/", arFiles);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @param ext      文件的扩展名
     * @throws IOException
     */

    public List<FTPFileBean> list(FTPClient client,String pathName, String ext, String item, List<FTPFileBean> list) {

        try {
            if (pathName.startsWith("/") && pathName.endsWith("/")) {
                FTPFileBean ffb = new FTPFileBean();
                String directory = pathName;
                //更换目录到当前目录
//            this.client.changeWorkingDirectory(directory);
                FTPFile[] files = client.listFiles(directory);
                for (FTPFile file : files) {
                    if (file.isFile()) {
                        if (file.getName().endsWith(ext)) {
                            ffb.setFileName(file.getName());
                            ffb.setItem(item);
                            ffb.setFilePath(directory + file.getName());
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            String dateStr = df.format(file.getTimestamp().getTime());
                            ffb.setTime(dateStr);
                            list.add(ffb);
                        }
                    } else if (file.isDirectory()) {
                        list(client,directory + file.getName() + "/", ext, item, list);
                    }
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载文件 *
     *
     * @param pathname FTP服务器文件目录 *
     * @return
     */
    public InputStream downloadFile(FTPClient client,String pathname) throws IOException {

        try {
            return client.retrieveFileStream(pathname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}