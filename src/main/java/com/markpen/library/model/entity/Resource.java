package com.markpen.library.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 电子资料馆资源表
 * @TableName resource
 */
@TableName(value ="resource")
@Data
public class Resource {
    /**
     * 资料ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 档案号（唯一标识）
     */
    private String archiveNumber;

    /**
     * 资料标题
     */
    private String title;

    /**
     * 资料类型（如PDF、DOCX、PPT等）
     */
    private String type;

    /**
     * 责任者（作者或上传者）
     */
    private String contributor;

    /**
     * 外键，关联 location.id
     */
    private Long locationId;

    /**
     * 文件访问URL或服务器路径
     */
    private String fileUrl;

    /**
     * 封面图链接（可选）
     */
    private String coverUrl;

    /**
     * 评论路径
     */
    private String commentPaths;

    /**
     * 资料简介
     */
    private String description;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 下载次数统计
     */
    private Integer downloadCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}