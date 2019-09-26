package com.ljc.apkdown.utils.ftpPool;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FTPFileBean {
    private String fileName;
    private String filePath;
    private String item;
    private String time;
}
