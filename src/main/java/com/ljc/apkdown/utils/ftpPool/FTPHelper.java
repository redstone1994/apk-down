package com.ljc.apkdown.utils.ftpPool;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    public static void main(String[] args) throws IOException {
        List list=new ArrayList();
        FTPHelper ftpHelper=new FTPHelper();
        ftpHelper.ftpList("/",list);
    }

}
