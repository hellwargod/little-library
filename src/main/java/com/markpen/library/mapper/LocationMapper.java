package com.markpen.library.mapper;

import com.markpen.library.model.entity.Location;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author markpen
* @description 针对表【location(存放地点表)】的数据库操作Mapper
* @createDate 2025-06-13 17:33:04
* @Entity com.markpen.library.model.entity.Location
*/
public interface LocationMapper extends BaseMapper<Location> {
    @Select("SELECT id FROM location WHERE locationName = #{locationName}")
    Long getLocationIdByName(@Param("locationName") String locationName);
}




