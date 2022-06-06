package com.ggl.cloud.controller;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggl.cloud.entity.CommonResult;
import com.ggl.cloud.entity.Video;
import com.ggl.cloud.feignservice.ServerFeign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("user/video")
public class VideoController {
    @Resource
    private ServerFeign service;
    private ObjectMapper om=new ObjectMapper();
    @PostMapping("upload")
    public CommonResult upload(MultipartFile uploadVideo,@RequestParam("video")String video) throws IOException{
        File file=File.createTempFile(uploadVideo.getOriginalFilename(), ".mp4");
        uploadVideo.transferTo(file);
        return service.uploadVideo(file, video);
    }
    @GetMapping("delete")
    public CommonResult deleteVideo(@RequestBody Video video) {
        return service.delete(video);
    }

    @PostMapping("select_page")
    public CommonResult selectPage(int pageNumber,int pageSize,@RequestBody Video video) {
        ConcurrentHashMap<String,Object> map=new ConcurrentHashMap<>();
        map.put("pageNumber", pageNumber);
        map.put("pageSize", pageSize);
        map.put("video", video);
        return service.selectPageVideo(map);

    }

    
}