/*
*
*@Date:2022年5月07日
*
*@Author:Lori Shu
*
*/
package com.ggl.cloud.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggl.cloud.config.BlockHandlerClass;
import com.ggl.cloud.entity.CommonResult;
import com.ggl.cloud.entity.Music;
import com.ggl.cloud.mapper.MusicMapper;
import com.ggl.cloud.service.IMusicService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@Transactional
public class MusicServiceImpl extends ServiceImpl<MusicMapper,Music> implements IMusicService {
    @Resource
    MusicMapper mapper;
        @Override
        @CachePut(value = "uploadMusic",key = "#music.userId")
        @SentinelResource(value = "uploadMusic",blockHandler = "defaultBlock",blockHandlerClass = {BlockHandlerClass.class})
    public CommonResult uploadMusic(Music music){
            if(save(music)){
                return CommonResult.builder().code(CommonResult.SUCCESS).detail("上传音乐成功").build();
            }
            throw new RuntimeException("保存音乐记录出现了问题！");
    }
    @Override
    @CacheEvict(value = "musicSelectPage",key = "#music.userId")
    @SentinelResource(value = "deleteMusic",blockHandler = "defaultBlock",blockHandlerClass = {BlockHandlerClass.class})
    public CommonResult deleteMusic(Music music){
            if(removeById(music)){
                return CommonResult.builder().code(CommonResult.SUCCESS).detail("删除音乐成功").build();
            }
            throw new RuntimeException("删除音乐记录出现问题");
    }
@Override
@Cacheable(value = "musicSelectPage",key = "#music.userId")
    public CommonResult selectMusicPage(int pageNumber,int pageSize,Music music){
        Page<Music> musicPage=new Page<>(pageNumber,pageSize);
        QueryWrapper<Music> queryWrapper=new QueryWrapper<>();
        if(StringUtils.isEmpty(music.getUserId())){
            throw new RuntimeException("userId不能为空");
        }
        if(!StringUtils.isEmpty(music.getMusicName())){
            queryWrapper.like("music_name", music.getMusicName());
        }
        if(!StringUtils.isEmpty(music.getUserId())){
            queryWrapper.like("user_id", music.getUserId());
        }

        if(!StringUtils.isEmpty(music.getMusician())){
            queryWrapper.like("musician", music.getMusician());
        }
        if(!StringUtils.isEmpty(music.getAlbum())){
            queryWrapper.like("album", music.getAlbum());
        }
        page(musicPage, queryWrapper);
        if(musicPage.getRecords().size()>0){
            return CommonResult.builder()
            .code(CommonResult.SUCCESS)
            .detail("查询音乐页面成功")
            .result(musicPage.getRecords())
            .build();
        }
        throw new RuntimeException("查询音乐页面出现问题，结果为空！");
    }
@Override
public CommonResult getStatistics() {
    // TODO Auto-generated method stub
    LocalDateTime nowTime=LocalDateTime.now().plusDays(-1);
    DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String s=dateTimeFormatter.format(nowTime);
    int uploadCount=mapper.getUploadCount(s);
    log.warn("uploadCount"+uploadCount);
    int deleteCount=mapper.getDeleteCount(s);
    log.warn("deleteCount"+deleteCount);
    Map<String,Integer> resultMap=new ConcurrentHashMap<>();
    resultMap.put("musicUploadCount", uploadCount);
    resultMap.put("musicDeleteCount", deleteCount);
    return CommonResult.builder().code(CommonResult.SUCCESS).detail("统计成功").result(resultMap).build();
}
    
}