package com.markpen.library.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markpen.library.annotation.AuthCheck;
import com.markpen.library.common.BaseResponse;
import com.markpen.library.common.ResultUtils;
import com.markpen.library.constant.UserConstant;
import com.markpen.library.exception.BusinessException;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.exception.ThrowUtils;
import com.markpen.library.model.dto.libraryResource.LibraryResourceCreateRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceDeleteRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceQueryRequest;
import com.markpen.library.model.dto.libraryResource.LibraryResourceUpdateRequest;
import com.markpen.library.model.entity.Comment;
import com.markpen.library.model.entity.LibraryResource;
import com.markpen.library.model.vo.LibraryResourceVO;
import com.markpen.library.model.vo.UserVO;
import com.markpen.library.service.LibraryresourceService;
import com.markpen.library.service.impl.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
     * @param locationName
     * @param coverUrl
     * @param description
     * @param newFile
     * @return
     */
    @PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<LibraryResourceVO> createResource(
            @RequestPart("title") String title,
            @RequestPart("locationName") String locationName,
            @RequestPart(value = "coverUrl", required = false) String coverUrl,
            @RequestPart(value = "description", required = false) String description,
            @RequestPart("newFile") MultipartFile newFile,
            HttpServletRequest request) {

        // contributor锁定为当前登录用户
        UserVO loginUser = userServiceImpl.getLoginUser(request);
        String contributor = loginUser.getUserName();

        // 自动获取文字后缀
        String originalFilename = newFile.getOriginalFilename();
        String type = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            type = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

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
                Long.valueOf(updateRequest.getId()),
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

        boolean result = libraryresourceService.deleteResource(Long.valueOf(deleteRequest.getId()));

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



    @GetMapping("/download")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public void downloadFile(@RequestParam String id, HttpServletResponse response) {
        try {
            // 调用 Service 获取 Resource

            Resource resource = libraryresourceService.downloadResourceById(Long.valueOf(id));

            // 设置响应头
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String filename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8.name());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

            // 设置 Content-Length（如果知道文件大小的话）
            if (resource.contentLength() > 0) {
                response.setContentLengthLong(resource.contentLength());
            }

            // 流式输出文件内容
            try (InputStream is = resource.getInputStream();
                 OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "传输失败");
        }
    }

    /**
     * 新增评论
     *
     * @param resourceId 资源ID
     * @param commentText 评论内容
     * @param request 当前登录用户信息
     * @return 成功提示
     */
    @PostMapping("/addComment")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public BaseResponse<String> addComment(
            @RequestParam("resourceId") String resourceId,
            @RequestParam("commentText") String commentText,
            HttpServletRequest request) throws IOException {

        // 获取当前登录用户
        UserVO loginUser = userServiceImpl.getLoginUser(request);
        Long userId = loginUser.getId();
        String userName = loginUser.getUserName();

        // 调用服务层添加评论
        boolean result = libraryresourceService.addCommentById(commentText, Long.valueOf(resourceId), userId, userName);

        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "评论失败");
        }

        return ResultUtils.success("评论成功");
    }

    /**
     * 获取资源的评论内容（返回 JSON 文件）
     *
     * @param resourceId 资源ID
     * @return 返回评论文件 Resource
     */
    @GetMapping("/getComment")
    @AuthCheck(mustRole = UserConstant.USER_LOGIN_STATE)
    public BaseResponse<List<Comment>> getComment(@RequestParam("resourceId") String resourceId) {
        // 获取评论（从文件中读取 JSON → 解析成 List<Comment>）
        Resource commentResource = libraryresourceService.getCommentById(Long.valueOf(resourceId));

        try (InputStream inputStream = commentResource.getInputStream()) {
            // 假设文件内容是 JSON 数组，使用 Jackson 解析
            ObjectMapper objectMapper = new ObjectMapper();
            List<Comment> commentList = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Comment>>() {}
            );
            return ResultUtils.success(commentList);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取评论文件失败");
        }
    }
}
