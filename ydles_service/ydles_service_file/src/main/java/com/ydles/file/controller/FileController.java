package com.ydles.file.controller;

import com.ydles.entity.Result;
import com.ydles.entity.StatusCode;
import com.ydles.file.utils.FastDFSClient;
import com.ydles.file.utils.FastDFSFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Scz
 * @date 2022/4/11 20:36
 */
@RestController
@RequestMapping("file")
public class FileController {
    @PostMapping("upload")
    public Result<Object> upload(MultipartFile file){
        try{
            String originalFilename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();
            String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            FastDFSFile upfile = new FastDFSFile(originalFilename, bytes, ext);
            String[] upload = FastDFSClient.upload(upfile);
            String url = FastDFSClient.getTrackerUrl() + upload[0] + "/" + upload[1];
            return new Result<>(true, StatusCode.OK, "上传成功", url);
        }catch (IOException e) {
            return new Result<>(false, StatusCode.ERROR, "上传失败" + e.getMessage());
        }
    }
}
