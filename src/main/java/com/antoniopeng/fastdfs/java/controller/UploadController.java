package com.antoniopeng.fastdfs.java.controller;

import com.antoniopeng.fastdfs.java.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 360000)
@RestController
public class UploadController {

    @Value("${fastdfs.base.url}")
    private String FASTDFS_BASE_URL;

    @Autowired
    private StorageService storageService;

    /**
     * 文件上传
     *
     * @param file  单文件
     * @param files 多文件
     * @return
     */
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public Map<String, Object> upload(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "files") MultipartFile[] files) {
        Map<String, Object> result = new HashMap<>();

        // 单文件 上传
        if (file != null) {
            result.put("fileName", writeFile(file));
        }

        // 多文件 上传
        if (files != null && files.length > 0) {
            List<String> fileNames = new ArrayList<>();

            for (MultipartFile multipartFile : files) {
                fileNames.add(writeFile(multipartFile));
            }

            result.put("errno", 0);
            result.put("fileNames", fileNames);
        }

        return result;
    }

    /**
     * 将图片写入指定目录
     *
     * @param multipartFile
     * @return 返回文件完整路径
     */
    private String writeFile(MultipartFile multipartFile) {
        // 获取文件后缀
        String oName = multipartFile.getOriginalFilename();
        String extName = oName.substring(oName.lastIndexOf(".") + 1);

        // 文件存放路径
        String url = null;
        try {
            String uploadUrl = storageService.upload(multipartFile.getBytes(), extName);
            url = FASTDFS_BASE_URL + uploadUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 返回文件完整路径
        return url;
    }
}