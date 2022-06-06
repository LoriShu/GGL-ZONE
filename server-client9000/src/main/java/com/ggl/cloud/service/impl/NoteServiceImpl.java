package com.ggl.cloud.service.impl;

import com.ggl.cloud.config.BlockHandlerClass;
import com.ggl.cloud.entity.CommonResult;
import com.ggl.cloud.entity.Note;
import com.ggl.cloud.mapper.NoteMapper;
import com.ggl.cloud.service.INoteService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baomidou
 * @since 2022-05-01
 */
@Service
@Slf4j
@Transactional
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements INoteService {
    @Override
    @CachePut(value = "insertNote",key = "#note.userId")
    @SentinelResource(value = "insertNote",blockHandler = "defaultBlock",blockHandlerClass = {BlockHandlerClass.class})
    public CommonResult insertNote(Note note){
        if(save(note)){
            return CommonResult.builder().code(CommonResult.SUCCESS).detail("插入成功").build();
        }
        return CommonResult.builder().code(CommonResult.ERROR).detail("插入失败").build();
    }

    @Override
    @CacheEvict(value = "selectPageNote",key = "#note.userId")
    @SentinelResource(value = "deleteNote",blockHandler = "defaultBlock",blockHandlerClass = {BlockHandlerClass.class})
    public CommonResult deleteNote(Note note) {
        // TODO Auto-generated method stub
        if(removeById(note)){
            return CommonResult.builder().code(CommonResult.SUCCESS).detail("删除笔记成功").build();
        }
        throw new RuntimeException("删除笔记失败");
    }

    @Override
    @CacheEvict(value = "selectPageNote",key = "#note.userId")
    @SentinelResource(value = "updateNote",blockHandler = "defaultBlock",blockHandlerClass = {BlockHandlerClass.class})
    public CommonResult updateNote(Note note) {
        // TODO Auto-generated method stub
             // 先查再改乐观锁才起效,version必须为查出来的值，否则会修改失败
            Note thisNote=getById(note.getId());
            note.setVersion(thisNote.getVersion());
            // thisNote.setContent("我要修改content");
            if(updateById(note)){
                return CommonResult.builder().code(CommonResult.SUCCESS).detail("修改成功").build();
            }
        throw new RuntimeException("修改出现异常");
    }

    @Override
    @Cacheable(value = "selectPageNote",key = "#note.userId")
    public CommonResult selectPage(int pageNumber, int pageSize, Note note) {
        // TODO Auto-generated method stub
        if(StringUtils.isEmpty(note.getUserId())){
            throw new RuntimeException("userId不能为空");
        }
        Page<Note> page=new Page<>(pageNumber,pageSize);
        QueryWrapper<Note> queryWrapper=new QueryWrapper<>();
        if(!StringUtils.isEmpty(note.getTitle())){
            queryWrapper.like("title", note.getTitle());
        }
        if(!StringUtils.isEmpty(note.getUserId())){
            queryWrapper.like("user_id", note.getUserId());
        }

        if(!StringUtils.isEmpty(note.getYear())){
            queryWrapper.like("year", note.getYear());
        }
        if(!StringUtils.isEmpty(note.getMonth())){
            queryWrapper.like("month", note.getMonth());
        }
        if(!StringUtils.isEmpty(note.getDay())){
            queryWrapper.like("day", note.getDay());
        }
        if(!StringUtils.isEmpty(note.getContent())){
            queryWrapper.like("content", note.getContent());
        }
        
        page(page,queryWrapper); 
        log.warn(note.toString());
        // System.out.println(page.getRecords());

    //    log.info(splitPage.getPageList().toString());
        if(page.getSize()>0){
            log.warn(page.getRecords().toString());
            return CommonResult.builder().code(CommonResult.SUCCESS).detail("查询成功").result(page.getRecords()).build();
        }
        throw new RuntimeException("查询过程出现问题！");
    } 

}