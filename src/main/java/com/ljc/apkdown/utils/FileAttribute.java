package com.ljc.apkdown.utils;

import java.util.Date;

public class FileAttribute {
    private Integer fileId;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String filePath;
    private Integer isfile=1;
    private Integer parentId;
    private Date fileCreateTime;
    private Date createTime;
    public FileAttribute() {
    }

    public Date getFileCreateTime() {
        return fileCreateTime;
    }

    public void setFileCreateTime(Date fileCreateTime) {
        this.fileCreateTime = fileCreateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getIsfile() {
        return isfile;
    }

    public void setIsfile(Integer isfile) {
        this.isfile = isfile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "父目录，"+parentId+",id"+fileId+",文件名称"+fileName+",文件类型，"+fileType+",文件大小，"+fileSize+",文件地址，"+filePath+",文件生成时间"+fileCreateTime;
    }
}