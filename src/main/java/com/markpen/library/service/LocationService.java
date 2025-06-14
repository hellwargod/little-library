package com.markpen.library.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.markpen.library.model.dto.location.LocationQueryRequest;
import com.markpen.library.model.entity.Location;
import com.baomidou.mybatisplus.extension.service.IService;
import com.markpen.library.model.vo.LocationVO;

import java.util.List;

/**
* @author markpen
* @description 针对表【location(存放地点表)】的数据库操作Service
* @createDate 2025-06-13 16:29:07
*/
public interface LocationService extends IService<Location> {

    /**
     * 新建地点
     *
     * @param locationName
     * @param geolocation
     * @return
     */
    LocationVO locationCreate (String locationName, String geolocation);

    /**
     * 修改地点信息
     *
     * @param locationName      需修改的地点名
     * @param newLocationName   新的地点名
     * @param newGeolocation    新的地理位置
     * @return
     */
    LocationVO locationUpdate (String locationName, String newLocationName, String newGeolocation);

    /**
     * 删除地点
     *
     * @param locationName 需删除的地点名
     * @return
     */
    boolean locationDelete (String locationName);

    /**
     * 返回脱敏地点
     *
     * @param location
     * @return
     */
    LocationVO convertToV0 (Location location);

    /**
     * 获取查询目标信息
     *
     * @param locationQueryRequest
     * @return
     */
    Wrapper<Location> getQueryWrapper(LocationQueryRequest locationQueryRequest);

    /**
     * 返回脱敏后地点信息列表
     *
     * @param locationList
     * @return
     */
    List<LocationVO> getLocationVOList(List<Location> locationList);

}
