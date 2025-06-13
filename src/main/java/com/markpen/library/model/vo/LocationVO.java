package com.markpen.library.model.vo;

import lombok.Data;

@Data
public class LocationVO {
    /**
     * 地点ID
     */
    private Long id;

    /**
     * 地点名称
     */
    private String locationName;

    /**
     * 地理位置（如经纬度或地址）
     */
    private String geolocation;
}
