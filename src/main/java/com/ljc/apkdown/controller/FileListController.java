package com.ljc.apkdown.controller;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ljc.apkdown.utils.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Slf4j
public class FileListController {


    @GetMapping("/qrc/{item}")
    @ResponseBody
    public void qrcController(HttpServletResponse response,@PathVariable(value = "item") String item){
        log.info(item);
        String qrc="http://192.168.1.190/fileList";
        BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream(qrc, response);
        // 将二维码输出到页面中
        try {
            MatrixToImageWriter.writeToStream(qRcodeImg ,"png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/fileList")
    public void fileList(String item){

    }
}
