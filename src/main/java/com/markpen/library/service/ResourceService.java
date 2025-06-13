package com.markpen.library.service;

import com.markpen.library.model.entity.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author markpen
* @description 针对表【resource(电子资料馆资源表)】的数据库操作Service
* @createDate 2025-06-13 16:40:43
*/
public interface ResourceService extends IService<Resource> {

    /**
     * 检查locationId是否存在关联资源
     *
     * @param locationId
     * @return
     */
    Long countByLocationId(Long locationId);
}
