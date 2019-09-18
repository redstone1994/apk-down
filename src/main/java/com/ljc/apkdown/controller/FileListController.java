package com.ljc.apkdown.controller;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ljc.apkdown.utils.QRCodeUtil;
import com.ljc.apkdown.utils.ftpPool.FTPHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class FileListController {

    @GetMapping("/qrc/{item}")
    @ResponseBody
    public void qrcController(HttpServletResponse response,@PathVariable(value = "item") String item){
        log.info(item);
        String downPage="http://192.168.1.190/fileList/"+item;
        BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream(downPage, response);
        // 将二维码输出到页面中
        try {
            MatrixToImageWriter.writeToStream(qRcodeImg ,"png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fileList/{item}")
    @ResponseBody
    public String fileList(@PathVariable(value = "item")String item){
        FTPHelper ftpHelper=new FTPHelper();
        List<String> arFiles=new ArrayList<>();
        if (item.isEmpty()){
            return "非法访问！！！";
        }else {

            try {
                ftpHelper.ftpList("/"+item+"/", arFiles);
                log.info(arFiles.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return "没有这个项目";
            }
            return arFiles.toString();
        }
    }

    @GetMapping("/down")
    @ResponseBody
    public void downLoad(HttpServletResponse response,String fileName){
        FTPHelper ftpHelper=new FTPHelper();

    }
}
