package com.ljc.apkdown.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class GetAllDirectory {
    private static Logger logger = LoggerFactory.getLogger(GetAllDirectory.class);

    /*
     * path 要遍历的目录
     * return
     */
    public List showDirectory(String path) throws IOException {
        List<FileBean> data = new ArrayList<>();

        for (File f:traverseFolder(path)) {
            if (f.getName().endsWith(".apk")){
                FileBean fb = new FileBean();
                fb.setName(f.getName());
                fb.setPath(f.getAbsolutePath());
                System.out.print("path=========="+f.getCanonicalPath());
                Calendar cal = Calendar.getInstance();
                Long time = f.lastModified();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cal.setTimeInMillis(time);
                String ltime = formatter.format(cal.getTime());
                fb.setTime(ltime);
                data.add(fb);
            }
        }
        return data;
    }

    private List<File> traverseFolder(String path) {
        int fileNum = 0, folderNum = 0;
        File file = new File(path);
        List<File> flist=new ArrayList();
        if (file.exists()) {
            LinkedList<File> list = new LinkedList<>();
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    logger.info("文件夹:" + file2.getAbsolutePath());
                    list.add(file2);
                    fileNum++;
                } else {
                    logger.info("文件:" + file2.getAbsolutePath());
                    flist.add(file2);
                    folderNum++;
                }
            }
            File temp_file;
            while (!list.isEmpty()) {
                temp_file = list.removeFirst();
                files = temp_file.listFiles();
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        logger.info("文件夹:" + file2.getAbsolutePath());
                        list.add(file2);
                        fileNum++;
                    } else {
                        logger.info("文件:" + file2.getAbsolutePath());
                        folderNum++;
                        flist.add(file2);
                    }
                }
            }

        } else {
            logger.error("文件不存在!");
        }
        logger.info("文件夹共有:" + folderNum + ",文件共有:" + fileNum);
        return flist;
    }

}
