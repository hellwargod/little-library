package com.markpen.library.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markpen.library.model.dto.libraryResource.LibraryResourceCreateRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceDeleteRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceQueryRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceUpdateRequest;
import com.markpen.library.model.entity.LibraryResource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.markpen.library.model.vo.LibraryResourceVO;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
* @author markpen
* @description 针对表【libraryresource(电子资料馆资源表)】的数据库操作Service
* @createDate 2025-06-13 19:21:59
*/
public interface LibraryresourceService extends IService<LibraryResource> {

    String storeFile(MultipartFile file) throws IOException;

    UrlResource loadFileAsResource(String fileName) throws MalformedURLException;

    /**
     * 上传资源
     */
    LibraryResourceVO uploadResource(
            String title,
            String type,
            String contributor,
            String locationName,
            String coverUrl,
            String description,
            MultipartFile file);

    /**
     * 更新资源信息
     */
    LibraryResourceVO updateResource(
            Long id,
            String newTitle,
            String newType,
            String newContributor,
            String newLocationName,
            String newCoverUrl,
            String newDescription,
            MultipartFile newFile);

    /**
     * 删除资源
     */
    boolean deleteResource(Long id);

    /**
     * 获取资源详情
     */
    LibraryResourceVO getResourceById(Long id);

    /**
     * 分页查询资源列表
     */
    Page<LibraryResourceVO> getResourceVOPage(LibraryResourceQueryRequest queryRequest);
    /**
     * 构建查询条件（供内部使用）
     */
    Wrapper<LibraryResource> getQueryWrapper(LibraryResourceQueryRequest queryRequest);

    /**
     * 数据脱敏
     *
     * @param resource
     * @return
     */
    LibraryResourceVO convertToVO(LibraryResource resource);
}
