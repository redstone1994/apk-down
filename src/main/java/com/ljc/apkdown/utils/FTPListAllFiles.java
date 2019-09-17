package com.ljc.apkdown.utils;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 列出FTP服务器上指定目录下面的所有文件
 */
public class FTPListAllFiles {
    public FTPClient ftp;
    public ArrayList<String> arFiles;

    /**
     * 重载构造函数
     *
     * @param isPrintCommmand 是否打印与FTPServer的交互命令
     */
    public FTPListAllFiles(boolean isPrintCommmand) {
        ftp = new FTPClient();
        arFiles = new ArrayList<String>();
        if (isPrintCommmand) {
            ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        }
    }

    /**
     * 登陆FTP服务器
     *
     * @param host     FTPServer IP地址
     * @param port     FTPServer 端口
     * @param username FTPServer 登陆用户名
     * @param password FTPServer 登陆密码
     * @return 是否登录成功
     * @throws IOException
     */
    public boolean login(String host, int port, String username, String password) throws IOException {
        this.ftp.connect(host, port);
        if (FTPReply.isPositiveCompletion(this.ftp.getReplyCode())) {
            if (this.ftp.login(username, password)) {
                /**
                 需要注意这句代码，如果调用List()方法出现，文件的无线递归，与真实目录结构不一致的时候，可能就是因为转码后，读出来的文件夹与正式文件夹字符编码不一致所导致。
                 则需去掉转码，尽管递归是出现乱码，但读出的文件就是真实的文件，不会死掉。等读完之后再根据情况进行转码。
                 如果ftp部署在windows下，则：
                 for (String arFile : f.arFiles) {
                 arFile = new String(arFile.getBytes("iso-8859-1"), "GBK");
                 logger.info(arFile);
                 }
                 */
                this.ftp.setControlEncoding("GBK");
                return true;
            }
        }
        if (this.ftp.isConnected()) {
            this.ftp.disconnect();
        }
        return false;
    }

    /**
     * 关闭数据链接
     *
     * @throws IOException
     */
    public void disConnection() throws IOException {
        if (this.ftp.isConnected()) {
            this.ftp.disconnect();
        }
    }

    public List<FileAttribute>  setFileAttributeList(List<FileAttribute> fileAttributeList,FileAttribute fileAttribute,FTPFile file,String filePath){
        fileAttribute.setFileName(file.getName());
        if (file.getName().indexOf(".")>0){
            fileAttribute.setFileType(file.getName().substring(file.getName().lastIndexOf(".")+1));
        }
        fileAttribute.setFileSize(file.getSize());
        fileAttribute.setFileCreateTime(file.getTimestamp().getTime());
        fileAttribute.setFilePath(filePath+file.getName());
        fileAttribute.setCreateTime(new Date());
        fileAttributeList.add(fileAttribute);
        return fileAttributeList;
    }

    /**
     * 递归遍历出目录下面所有文件
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @throws IOException
     */
    public void List(String pathName, List<FileAttribute> fileAttributeList,Integer parentId,Integer id) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            this.ftp.changeWorkingDirectory(pathName);
            FTPFile[] files = this.ftp.listFiles();
            List<FileAttribute> dirfileAttributeList =new ArrayList<>();
            for (FTPFile file : files) {
                FileAttribute  fileAttribute=new FileAttribute();
                fileAttribute.setParentId(parentId);
                if (file.isFile()) {
                    fileAttribute.setIsfile(1);
                    //组装文件类
                    setFileAttributeList(fileAttributeList,fileAttribute,file,pathName);
                    System.out.println(file.getRawListing());
                    // arFiles.add(pathName + file.getName());
                } else if (file.isDirectory()) {
                    id++;
                    System.out.println(file.getRawListing());
                    fileAttribute.setFileId(id);
                    fileAttribute.setFileName(file.getName());
                    fileAttribute.setIsfile(0);
                    fileAttribute.setFileCreateTime(file.getTimestamp().getTime());
                    fileAttribute.setFilePath(pathName+file.getName());
                    fileAttribute.setCreateTime(new Date());
                    fileAttributeList.add(fileAttribute);
                    dirfileAttributeList.add(fileAttribute);

                }
            }

            if (dirfileAttributeList!=null&&dirfileAttributeList.size()>0){
                for ( FileAttribute  fileAttribute :dirfileAttributeList){
                    //save(fileAttribute);保存数据库返回主键id
                    parentId=fileAttribute.getFileId();
                    // 需要加此判断。否则，ftp默认将‘项目文件所在目录之下的目录（./）’与‘项目文件所在目录向上一级目录下的目录（../）’都纳入递归，这样下去就陷入一个死循环了。需将其过滤掉。
                    if (!".".equals(fileAttribute.getFileName()) && !"..".equals(fileAttribute.getFileName())) {
                        List(pathName + fileAttribute.getFileName() + "/",fileAttributeList,parentId,id);
                    }
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
    public void List(String pathName, String ext) throws IOException {
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            this.ftp.changeWorkingDirectory(pathName);
            FTPFile[] files = this.ftp.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(ext)) {
                        arFiles.add(pathName + file.getName());
                    }
                } else if (file.isDirectory()) {
                    if (!".".equals(file.getName()) && !"..".equals(file.getName())) {
                        List(pathName + file.getName() + "/", ext);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        FTPListAllFiles f = new FTPListAllFiles(true);
        List<FileAttribute> fileAttributeList=new ArrayList<FileAttribute>();
        Integer parentId=1;//根目录
        Integer id=2;//变量需要
        if (f.login("10.10.10.11", 21, "jenkins", "wm2012dx")) {
            f.List("/",fileAttributeList, parentId,id);
        }
        f.disConnection();
        for (FileAttribute fileAttribute : fileAttributeList) {
            System.out.println(fileAttribute.toString());
        }
        for (String arFile : f.arFiles) {
            System.out.println(arFile);
        }

    }
}
