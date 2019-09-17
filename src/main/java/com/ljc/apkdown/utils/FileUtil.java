package com.ljc.apkdown.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void download(HttpServletResponse res, String path, String fileName) throws IOException {
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        res.setCharacterEncoding("utf-8");
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
//        File f = new File(path + File.separator + fileName);
        File f = new File(path);
        res.addHeader("Content-Length", f.length() + "");
        try {
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(f));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (os!=null){
                try {
                    os.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
