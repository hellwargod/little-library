package com.markpen.library.mapper;

import com.markpen.library.model.entity.LibraryResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author markpen
* @description 针对表【libraryresource(电子资料馆资源表)】的数据库操作Mapper
* @createDate 2025-06-13 19:21:59
* @Entity com.markpen.library.model.entity.Libraryresource
*/
public interface LibraryresourceMapper extends BaseMapper<LibraryResource> {
    @Select("SELECT COUNT(*) FROM libraryresource WHERE location_id = #{locationId}")
    Long countByLocationId(@Param("locationId") Long locationId);
}




