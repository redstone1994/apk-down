package com.ljc.apkdown.controller;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.ljc.apkdown.utils.QRCodeUtil;
import org.apache.catalina.connector.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class FileListController {


    @GetMapping("/qrc")
    @ResponseBody
    public void qrcController(HttpServletResponse response){

        BitMatrix qRcodeImg = QRCodeUtil.generateQRCodeStream("ssss", response);
        // 将二维码输出到页面中
        try {
            MatrixToImageWriter.writeToStream(qRcodeImg ,"png", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
