package com.ljc.apkdown.controller;

import com.alibaba.fastjson.JSON;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ljc.apkdown.utils.QRCodeUtil;
import com.ljc.apkdown.utils.ftpPool.FTPFileBean;
import com.ljc.apkdown.utils.ftpPool.FTPHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class FileListController {

    FTPHelper ftpHelper = new FTPHelper();

    @GetMapping("/qrc/{item}")
    @ResponseBody
    public void qrcController(HttpServletResponse response, @PathVariable(value = "item") String item) {

        String downPage = "http://192.168.1.190:8080/apk-down/fileList/" + item;
        BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream(downPage, response);
        // 将二维码输出到页面中
        try {
            MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fileList/{item}")
    @ResponseBody
    public String fileList(@PathVariable(value = "item") String item) {

        List<FTPFileBean> arFiles;
        Map fileInfo = new HashMap();
        if (StringUtils.isEmpty(item)) {
            return "非法访问！！！";
        } else {
            try {
                arFiles = ftpHelper.list("/" + item + "/", "apk", item);
                if (!arFiles.isEmpty()) {
                    fileInfo.put("code", "0");
                    fileInfo.put("msg", "success");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "没有这个项目";
            }
            fileInfo.put("count", arFiles.size());
            fileInfo.put("data", arFiles);
            return JSON.toJSONString(fileInfo);
        }
    }

    @GetMapping(value = "/down")
    public void downLoad(HttpServletResponse response, @RequestParam String filePath) throws UnsupportedEncodingException {

        response.setContentType("application/force-download");// 设置强制下载不打开
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        String file = new String("app.apk".getBytes("gbk"), "iso-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + file);//URLEncoder.encode(file,"GBK")
//        response.setCharacterEncoding("utf-8");

//        File f = new File(path + File.separator + fileName);
//        File f = new File();

        try {
            InputStream is = ftpHelper.downloadFile(filePath);
            BufferedInputStream bis = new BufferedInputStream(is);

            int len = -1;
            byte[] buffer = new byte[1024];
            OutputStream out = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            while ((len = bis.read(buffer)) > -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }
            bos.close();
            bis.close();
            out.close();
            is.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
