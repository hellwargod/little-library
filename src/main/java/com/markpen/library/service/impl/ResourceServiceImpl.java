package com.markpen.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markpen.library.model.entity.Resource;
import com.markpen.library.service.ResourceService;
import com.markpen.library.mapper.ResourceMapper;
import org.springframework.stereotype.Service;

/**
* @author markpen
* @description 针对表【resource(电子资料馆资源表)】的数据库操作Service实现
* @createDate 2025-06-13 16:40:43
*/
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource>
    implements ResourceService{

    @Override
    public Long countByLocationId(Long locationId) {
        QueryWrapper<Resource> wrapper = new QueryWrapper<>();
        wrapper.eq("location_id", locationId);
        return this.baseMapper.selectCount(wrapper);
    }
}




