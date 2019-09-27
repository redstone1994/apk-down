package com.ljc.apkdown.controller;

import com.alibaba.fastjson.JSON;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ljc.apkdown.utils.QRCodeUtil;
import com.ljc.apkdown.utils.ftpPool.FTPFileBean;
import com.ljc.apkdown.utils.ftpPool.FTPHelper;
import com.ljc.apkdown.utils.ftpPool.FtpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class FileListController {

    @Resource
    private FTPHelper ftpHelper;
    @Autowired
    FtpClient ftpClient;

    private static String QRCURL;
    @Value("${qrcUrl}")
    public void setQRCURL(String qurUrl) {
        QRCURL = qurUrl;
    }

    @GetMapping("/")
    @ResponseBody
    public String index(){
        return "FTP工具";
    }

    @GetMapping("/qrc/{item}")
    @ResponseBody
    public void qrcController(HttpServletResponse response, @PathVariable(value = "item") String item) {

        String downPage = QRCURL + "/fileList/" + item;
        BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream(downPage, response);
        // 将二维码输出到页面中
        try {
            MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fileList/{item}")
    public String fileList(HttpServletRequest request, @PathVariable(value = "item") String item, ModelMap model) {
        FTPClient client=ftpClient.getFTPClient();
        List<FTPFileBean> arFiles = new ArrayList<>();

        if (StringUtils.isEmpty(item)) {
            model.addAttribute("data", "访问错误!!!");
        } else {
            try {
                ftpHelper.list(client,"/" + item + "/", "apk", item,arFiles);
                if (!arFiles.isEmpty()) {
                    log.info(JSON.toJSONString(arFiles));
                    model.addAttribute("data",JSON.toJSONString(arFiles));
                }if (arFiles.isEmpty()){
                    model.addAttribute("data", "没有这个项目");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                ftpClient.closeFTP(client);
            }
        }
        return "ftpFile";
    }

    @GetMapping(value = "/down")
    public void downLoad(HttpServletRequest request,HttpServletResponse response, @RequestParam String filePath,@RequestParam String fileName) throws UnsupportedEncodingException {
        FTPClient client=ftpClient.getFTPClient();
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        String file = new String(fileName.getBytes("gbk"), "iso-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + file);//URLEncoder.encode(file,"GBK")
//        response.setCharacterEncoding("utf-8");

//        File f = new File(path + File.separator + fileName);
//        File f = new File();

        try {
            InputStream is = ftpHelper.downloadFile(client,filePath);
            log.info(request.getRemoteAddr()+"正在下载"+fileName);
            BufferedInputStream bis = new BufferedInputStream(is);
//            response.addHeader("Content-Length", is.available() + "");

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
            log.error("下载失败");
            throw new RuntimeException(e);
        }finally {
            ftpClient.closeFTP(client);
        }
    }

}
