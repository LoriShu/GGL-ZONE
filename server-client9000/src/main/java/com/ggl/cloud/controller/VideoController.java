package com.ggl.cloud.controller;


import java.io.File;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ggl.cloud.entity.CommonResult;
import com.ggl.cloud.entity.Video;
import com.ggl.cloud.service.IVideoService;
import com.ggl.cloud.service.feignservice.ResourceFeign;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("server/video")
public class VideoController {
    @Resource
    private IVideoService service;
    @Resource
    private ResourceFeign feign;
    private ObjectMapper om=new ObjectMapper();

    @PostMapping(value = "upload")
    // @PreAuthorize("hasAnyAuthority('admin')")
    public CommonResult uploadVideo(@RequestBody File uploadVideo,@RequestParam("video")String video) throws JsonProcessingException {

        // log.warn(video.toString())
        CommonResult result=feign.uploadVideo(uploadVideo,video);
        if(result.getCode()==CommonResult.SUCCESS){
            String videoString=om.writeValueAsString(result.getResult());
            return service.uploadVideo(om.readValue(videoString, Video.class));
        }
        throw new RuntimeException("储存视频发生异常！");
    }

    @PostMapping("delete")
    public CommonResult deleteVideo(@RequestBody Video video) {
        CommonResult result=feign.deleteVideo(video);
        if(result.getCode()==CommonResult.SUCCESS){
        return service.deleteVideo(video);
    }
    throw new RuntimeException("删除视频失败！");
    }

    @PostMapping("selectPage")
    public CommonResult selectVideoPage(@RequestBody Map<String,Object> videoPageMap) throws JsonMappingException, JsonProcessingException {
        int pageNumber=(int) videoPageMap.get("pageNumber");
        int pageSize=(int) videoPageMap.get("pageSize");
        Object videoObj=  videoPageMap.get("video");
        String videoString=om.writeValueAsString(videoObj);
        return service.selectVideoPage(pageNumber,pageSize,om.readValue(videoString, Video.class));
    }
}