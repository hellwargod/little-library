package com.markpen.library.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markpen.library.annotation.AuthCheck;
import com.markpen.library.common.BaseResponse;
import com.markpen.library.common.ResultUtils;
import com.markpen.library.constant.UserConstant;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.exception.ThrowUtils;
import com.markpen.library.model.dto.location.LocationCreateRequest;
import com.markpen.library.model.dto.location.LocationDeleteRequest;
import com.markpen.library.model.dto.location.LocationQueryRequest;
import com.markpen.library.model.dto.location.LocationUpdateRequest;
import com.markpen.library.model.entity.Location;
import com.markpen.library.model.vo.LocationVO;
import com.markpen.library.service.LocationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {
    @Resource
    private LocationService locationService;

    /**
     * 增加地点
     *
     * @param locationCreateRequest
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<LocationVO> addLocation(@RequestBody LocationCreateRequest locationCreateRequest) {
        ThrowUtils.throwIf(locationCreateRequest==null, ErrorCode.PARAMETER_ERROR);
        String locationName = locationCreateRequest.getLocationName();
        String geolocation = locationCreateRequest.getGeolocation();

        LocationVO result = locationService.locationCreate(locationName, geolocation);
        return ResultUtils.success(result);
    }

    /**
     * 修改地点信息
     *
     * @param locationUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<LocationVO> updateLocation(@RequestBody LocationUpdateRequest locationUpdateRequest) {
        ThrowUtils.throwIf(locationUpdateRequest==null, ErrorCode.PARAMETER_ERROR);
        String locationName = locationUpdateRequest.getLocationName();
        String newLocationName = locationUpdateRequest.getNewLocationName();
        String newGeolocation = locationUpdateRequest.getNewGeolocation();

        LocationVO result = locationService.locationUpdate(locationName, newLocationName, newGeolocation);
        return ResultUtils.success(result);
    }

    /**
     * 删除地点
     *
     * @param locationDeleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteLocation(@RequestBody LocationDeleteRequest locationDeleteRequest) {
        ThrowUtils.throwIf(locationDeleteRequest==null, ErrorCode.PARAMETER_ERROR);
        String locationName = locationDeleteRequest.getLocationName();

        boolean result = locationService.locationDelete(locationName);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取地点封装列表（仅管理员）
     *
     * @param locationQueryRequest 查询请求参数
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<LocationVO>> listUserVOByPage(@RequestBody LocationQueryRequest locationQueryRequest) {
        ThrowUtils.throwIf(locationQueryRequest == null, ErrorCode.PARAMETER_ERROR);
        long current = locationQueryRequest.getCurrent();
        long pageSize = locationQueryRequest.getPageSize();
        Page<Location> locationPage = locationService.page(new Page<>(current, pageSize),
                locationService.getQueryWrapper(locationQueryRequest));
        Page<LocationVO> locationVOPage = new Page<>(current, pageSize, locationPage.getTotal());
        List<LocationVO> locationVOList = locationService.getLocationVOList(locationPage.getRecords());
        locationVOPage.setRecords(locationVOList);
        return ResultUtils.success(locationVOPage);
    }
}
