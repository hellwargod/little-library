package com.markpen.library.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 存放地点表
 * @TableName location
 */
@TableName(value ="location")
@Data
public class Location {
    /**
     * 地点ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 地点名称
     */
    private String locationName;

    /**
     * 地理位置（如经纬度或地址）
     */
    private String geolocation;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}