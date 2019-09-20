package com.ljc.apkdown.utils.ftpPool;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class FTPFileBean {
    private String fileName;
    private String filePath;
    private String item;
    private Date time;
}
