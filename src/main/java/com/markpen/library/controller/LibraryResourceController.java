package com.markpen.library.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markpen.library.annotation.AuthCheck;
import com.markpen.library.common.BaseResponse;
import com.markpen.library.common.ResultUtils;
import com.markpen.library.constant.UserConstant;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.exception.ThrowUtils;
import com.markpen.library.model.dto.libraryResource.LibraryResourceCreateRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceDeleteRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceQueryRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceUpdateRequest;
import com.markpen.library.model.entity.LibraryResource;
import com.markpen.library.model.vo.LibraryResourceVO;
import com.markpen.library.model.vo.UserVO;
import com.markpen.library.service.LibraryresourceService;
import com.markpen.library.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/Resource")
public class LibraryResourceController {

    @Autowired
    private LibraryresourceService libraryresourceService;
    @Autowired
    private UserServiceImpl userServiceImpl;

    /**
     * 新增资料
     * @param title
     * @param type
     * @param contributor
     * @param coverUrl
     * @param description
     * @param newFile
     * @return
     */
    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<LibraryResourceVO> createResource(
            @RequestPart("title") String title,
            @RequestPart("type") String type,
            @RequestPart("contributor") String contributor,
            @RequestPart("coverUrl") String coverUrl,
            @RequestPart("description") String description,
            @RequestPart("newFile") MultipartFile newFile,
            HttpServletRequest request) {

        // 上传者锁定为当前登录用户
        UserVO loginUser = userServiceImpl.getLoginUser(request);
        String locationName = loginUser.getUserName();
        // 调用 Service 方法上传资源
        LibraryResourceVO result = libraryresourceService.uploadResource(
                title, type, contributor, locationName, coverUrl, description, newFile);

        return ResultUtils.success(result);
    }

    /**
     * 修改资料信息
     *
     * @param updateRequest 更新请求
     * @return 返回更新后的 VO
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<LibraryResourceVO> updateResource(@RequestBody LibraryResourceUpdateRequest updateRequest) {
        ThrowUtils.throwIf(updateRequest == null, ErrorCode.PARAMETER_ERROR);

        LibraryResourceVO result = libraryresourceService.updateResource(
                updateRequest.getId(),
                updateRequest.getNewTitle(),
                updateRequest.getNewType(),
                updateRequest.getNewContributor(),
                updateRequest.getNewLocationName(),
                updateRequest.getNewCoverUrl(),
                updateRequest.getNewDescription(),
                updateRequest.getNewFile()
        );

        return ResultUtils.success(result);
    }

    /**
     * 删除资料
     *
     * @param deleteRequest 删除请求
     * @return 成功与否
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteResource(@RequestBody LibraryResourceDeleteRequest  deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMETER_ERROR);

        boolean result = libraryresourceService.deleteResource(deleteRequest.getId());

        return ResultUtils.success(result);
    }

    /**
     * 分页查询资料列表（带筛选条件）
     *
     * @param queryRequest 查询请求参数
     * @return 分页 VO 列表
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public BaseResponse<Page<LibraryResourceVO>> listResourceVOByPage(@RequestBody LibraryResourceQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMETER_ERROR);

        // 直接调用 service 处理分页和 VO 转换
        Page<LibraryResourceVO> voPage = libraryresourceService.getResourceVOPage(queryRequest);

        return ResultUtils.success(voPage);
    }

    /**
     * 文件下载接口
     */
    @GetMapping("/download")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public ResponseEntity<Resource> downloadFile(@RequestParam Long id) {
        Resource resource = libraryresourceService.downloadResourceById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
