package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FTPHelper {



    /** 本地字符编码 */
    private static String LOCAL_CHARSET = "GBK";

    // FTP协议里面，规定文件名编码为iso-8859-1
    private static String SERVER_CHARSET = "ISO-8859-1";

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public void ftpList(String pathName, List<String> arFiles) throws IOException {

        FTPClient client=FTPPool.getFTPClient();
        client.enterLocalPassiveMode();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            String directory = pathName;
            //更换目录到当前目录
            client.changeWorkingDirectory(directory);
            FTPFile[] files = client.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    arFiles.add(directory + file.getName());

                } else if (file.isDirectory()) {
                    ftpList(directory + file.getName() + "/", arFiles);
                }
            }
            FTPPool.returnFTPClient(client);
        }
    }

    /**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @param ext      文件的扩展名
     * @throws IOException
     */

    public  List<FTPFileBean> list(String pathName, String ext, String item,List<FTPFileBean> list) throws IOException {

        FTPClient client=FTPPool.getFTPClient();
        client.enterLocalPassiveMode();
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
                    list(directory + file.getName() + "/", ext, item,list);
                }
            }
            FTPPool.returnFTPClient(client);
            return list;
        }
        FTPPool.returnFTPClient(client);
        return null;
    }

    /**
     * 下载文件 *
     *
     * @param pathname FTP服务器文件目录 *
     * @return
     */
    public InputStream downloadFile(String pathname) throws IOException {

        FTPClient client=FTPPool.getFTPClient();
        client.enterLocalPassiveMode();
        client.setFileType(FTP.BINARY_FILE_TYPE);
//        if (FTPReply.isPositiveCompletion(client.sendCommand("OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
//            LOCAL_CHARSET = "UTF-8";
//        }

//        client.setControlEncoding(LOCAL_CHARSET);

        log.info("开始下载文件="+pathname);
        try {
            return client.retrieveFileStream(pathname);//new String(pathname.getBytes(LOCAL_CHARSET), SERVER_CHARSET)
        } catch (IOException e) {
            e.printStackTrace();
            log.error("下载失败！！！");
        }
        FTPPool.returnFTPClient(client);
        return null;
    }

}