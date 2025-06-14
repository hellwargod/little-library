package com.markpen.library.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markpen.library.exception.BusinessException;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.mapper.LibraryresourceMapper;
import com.markpen.library.model.dto.location.LocationQueryRequest;
import com.markpen.library.model.entity.Location;
import com.markpen.library.model.vo.LocationVO;
import com.markpen.library.service.LocationService;
import com.markpen.library.mapper.LocationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author markpen
* @description 针对表【location(存放地点表)】的数据库操作Service实现
* @createDate 2025-06-13 16:29:07
*/
@Service
@Slf4j
public class LocationServiceImpl extends ServiceImpl<LocationMapper, Location>
    implements LocationService{

    @Autowired
    private LibraryresourceMapper libraryresourceMapper; // 使用 Mapper 替代 Service

    @Override
    public LocationVO locationCreate(String locationName, String geolocation) {
        // 检查参数
        if(StrUtil.hasBlank(locationName, geolocation)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"参数为空");
        }

        // 检查是否重复
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("locationName", locationName);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"地点已存在");
        }

        // 插入数据
        Location location = new Location();
        location.setLocationName(locationName);
        location.setGeolocation(geolocation);
        boolean saveResult = this.save(location);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库系统错误");
        }
        return convertToV0(location);
    }

    @Override
    public LocationVO locationUpdate(String locationName, String newLocationName, String newGeolocation) {
        // 检查参数
        if(StrUtil.hasBlank(locationName, newLocationName, newGeolocation)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"参数为空");
        }

        // 查询是否存在这个地点
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("locationName", locationName);
        Location oldLocation = this.baseMapper.selectOne(queryWrapper);

        if (oldLocation == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "该地点不存在");
        }

        // 如果新旧名字不一致
        if(!locationName.equals(newLocationName)){
            QueryWrapper<Location> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("locationName", newLocationName);
            long count = this.baseMapper.selectCount(queryWrapper1);
            if(count > 0){
                throw new BusinessException(ErrorCode.PARAMETER_ERROR,"新地点名称已存在，请更换地名");
            }
        }
        oldLocation.setLocationName(newLocationName);
        oldLocation.setGeolocation(newGeolocation);
        boolean updateResult = this.updateById(oldLocation);
        if(!updateResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库更新失败");
        }
        return convertToV0(oldLocation);
    }

    @Override
    public boolean locationDelete(String locationName) {
        // 先根据名称查出 ID
        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", locationName);
        Location location = this.baseMapper.selectOne(queryWrapper);

        if (location == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "地点不存在");
        }

        // 检查 resource 表中是否还有引用
        long count = libraryresourceMapper.countByLocationId(location.getId());
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该地点下还有资源，无法删除");
        }

        // 最后删除
        return this.removeById(location.getId());
    }

    @Override
    public LocationVO convertToV0(Location location) {
        if(location == null){
            return null;
        }
        LocationVO locationVO = new LocationVO();
        BeanUtil.copyProperties(location,locationVO);
        return locationVO;
    }

    @Override
    public Wrapper<Location> getQueryWrapper(LocationQueryRequest locationQueryRequest) {
        if(locationQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"请求参数为空");
        }
        Long id =locationQueryRequest.getId();
        String locationName = locationQueryRequest.getLocationName();
        String geolocation = locationQueryRequest.getGeolocation();
        String sortField = locationQueryRequest.getSortField();
        String sortOrder = locationQueryRequest.getSortOrder();

        QueryWrapper<Location> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.like(StrUtil.isNotBlank(locationName), "locationName", locationName);
        queryWrapper.like(StrUtil.isNotBlank(geolocation), "geolocation", geolocation);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);

        return queryWrapper;
    }

    @Override
    public List<LocationVO> getLocationVOList(List<Location> locationList) {
        if(CollUtil.isEmpty(locationList)){
            return null;
        }
        return locationList.stream().map(this::convertToV0).collect(Collectors.toList());
    }

}




