package com.markpen.library.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markpen.library.constant.FileStorageConstant;
import com.markpen.library.exception.BusinessException;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.mapper.LocationMapper;
import com.markpen.library.model.dto.libraryResource.LibraryResourceQueryRequest;
import com.markpen.library.model.entity.LibraryResource;
import com.markpen.library.model.vo.LibraryResourceVO;
import lombok.extern.slf4j.Slf4j;
import com.markpen.library.service.LibraryresourceService;
import com.markpen.library.mapper.LibraryresourceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
* @author markpen
* @description 针对表【libraryresource(电子资料馆资源表)】的数据库操作Service实现
* @createDate 2025-06-13 19:21:59
*/
@Service
@Slf4j
public class LibraryresourceServiceImpl extends ServiceImpl<LibraryresourceMapper, LibraryResource>
    implements LibraryresourceService{

    @Autowired
    private FileStorageConstant fileStorageConstant;

    @Autowired
    private LocationMapper locationMapper;

    /**
     * 加载文件为 Resource 对象
     */
    @Override
    public UrlResource loadFileAsResource(String fileName) throws MalformedURLException {
        Path fileStorageLocation = Paths.get(fileStorageConstant.getUploadDir()).toAbsolutePath().normalize();
        Path filePath = fileStorageLocation.resolve(fileName).normalize();
        UrlResource urlResource = new UrlResource(filePath.toUri());

        if (urlResource.exists()) {
            return urlResource;
        } else {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"文件不存在");
        }
    }

    /**
     * 存储文件到本地，并返回存储后的文件名
     */
    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // storeFile 方法开头加
        if (fileStorageConstant == null || fileStorageConstant.getUploadDir() == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件存储路径未配置");
        }
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        if (originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String storedFileName = UUID.randomUUID() + fileExtension;

        Path fileStorageLocation = Paths.get(fileStorageConstant.getUploadDir()).toAbsolutePath().normalize();

        // 创建目录（如果不存在）
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }

        Path targetLocation = fileStorageLocation.resolve(storedFileName);
        file.transferTo(targetLocation);

        return storedFileName;
    }


    @Override
    public LibraryResourceVO uploadResource(
            String title,
            String type,
            String contributor,
            String locationName,
            String coverUrl,
            String description,
            MultipartFile file) {

        // 参数校验
        if (file == null || StrUtil.hasBlank(title, type, contributor, locationName)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "必要参数为空");
        }

        // 查询地点ID
        Long locationId = locationMapper.getLocationIdByName(locationName); // 直接调用 Mapper

        // 存储文件
        String storedFileName;
        try {
            storedFileName = storeFile(file);
        } catch (IOException e) {
            log.error("文件存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }

        String fileUrl = "/api/file/" + storedFileName;

        // 计算文件大小
        long fileSize = file.getSize();

        // 生成档案号
        String archiveNumber = "ARCH-" + UUID.randomUUID().toString().substring(0, 8);

        // 构造实体
        LibraryResource resource = new LibraryResource();
        resource.setTitle(title);
        resource.setType(type);
        resource.setContributor(contributor);
        resource.setLocationId(locationId);
        resource.setFileUrl(fileUrl);
        resource.setCoverUrl(coverUrl);
        resource.setDescription(description);
        resource.setSize(fileSize);               // 文件大小
        resource.setArchiveNumber(archiveNumber); // 档案号


        // 插入数据库
        boolean saveResult = this.save(resource);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据库保存失败");
        }

        return convertToVO(resource);
    }

    @Override
    public LibraryResourceVO updateResource(
            Long id,
            String newTitle,
            String newType,
            String newContributor,
            String newLocationName,
            String newCoverUrl,
            String newDescription,
            MultipartFile newFile) {

        // 校验ID
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "资源ID不能为空");
        }

        // 查询原数据
        LibraryResource oldResource = this.getById(id);
        if (oldResource == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "该资源不存在");
        }

        // 如果修改了地点
        if (StrUtil.isNotBlank(newLocationName)) {
            Long newLocationId = locationMapper.getLocationIdByName(newLocationName);
            oldResource.setLocationId(newLocationId);
        }

        // 更新字段（非空才更新）
        if (StrUtil.isNotBlank(newTitle)) oldResource.setTitle(newTitle);
        if (StrUtil.isNotBlank(newType)) oldResource.setType(newType);
        if (StrUtil.isNotBlank(newContributor)) oldResource.setContributor(newContributor);
        if (StrUtil.isNotBlank(newCoverUrl)) oldResource.setCoverUrl(newCoverUrl);
        if (StrUtil.isNotBlank(newDescription)) oldResource.setDescription(newDescription);

        // 文件上传是可选的
        if (newFile != null && !newFile.isEmpty()) {
            String storedFileName;
            try {
                storedFileName = storeFile(newFile);
            } catch (IOException e) {
                log.error("文件上传失败", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
            }
            oldResource.setFileUrl("/api/file/" + storedFileName);
        }

        // 保存到数据库
        boolean updateResult = this.updateById(oldResource);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }

        return convertToVO(oldResource);
    }

    @Override
    public boolean deleteResource(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "资源ID不能为空");
        }
        return this.removeById(id);
    }

    @Override
    public LibraryResourceVO getResourceById(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "资源ID不能为空");
        }

        LibraryResource resource = this.getById(id);
        return convertToVO(resource);
    }

    @Override
    public Wrapper<LibraryResource> getQueryWrapper(LibraryResourceQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "请求参数为空");
        }

        String title = queryRequest.getTitle();
        String type = queryRequest.getType();
        String contributor = queryRequest.getContributor();
        String locationName = queryRequest.getLocationName();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        return buildQueryWrapper(title, type, contributor, locationName, sortField, sortOrder);
    }

    /**
     * 构建查询条件（供内部使用）
     */
    private Wrapper<LibraryResource> buildQueryWrapper(
            String title, String type, String contributor, String locationName, String sortField, String sortOrder) {

        QueryWrapper<LibraryResource> wrapper = new QueryWrapper<>();

        wrapper.like(StrUtil.isNotBlank(title), "title", title);
        wrapper.eq(StrUtil.isNotBlank(type), "type", type);
        wrapper.like(StrUtil.isNotBlank(contributor), "contributor", contributor);
        wrapper.eq(StrUtil.isNotBlank(locationName), "locationName", locationName);
        wrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder != null && sortOrder.equals("ascend"), sortField);

        return wrapper;
    }

    @Override
    public Page<LibraryResourceVO> getResourceVOPage(LibraryResourceQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "请求参数为空");
        }

        int current = queryRequest.getCurrent();
        int pageSize = queryRequest.getPageSize();
        String title = queryRequest.getTitle();
        String type = queryRequest.getType();
        String contributor = queryRequest.getContributor();
        String locationName = queryRequest.getLocationName();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        // 构造查询条件
        Wrapper<LibraryResource> wrapper = buildQueryWrapper(title, type, contributor, locationName, sortField, sortOrder);

        // 分页查询
        Page<LibraryResource> resourcePage = this.page(new Page<>(current, pageSize), wrapper);

        // 转换为 VO
        List<LibraryResourceVO> voList = resourcePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<LibraryResourceVO> voPage = new Page<>();
        voPage.setCurrent(current);
        voPage.setSize(pageSize);
        voPage.setTotal(resourcePage.getTotal());
        voPage.setPages(resourcePage.getPages());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public LibraryResourceVO convertToVO(LibraryResource resource) {
        if (resource == null) {
            return null;
        }
        LibraryResourceVO vo = new LibraryResourceVO();
        BeanUtil.copyProperties(resource, vo);
        return vo;
    }
}




