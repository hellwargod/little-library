package com.markpen.library.controller;

import com.markpen.library.annotation.AuthCheck;
import com.markpen.library.common.BaseResponse;
import com.markpen.library.common.ResultUtils;
import com.markpen.library.constant.UserConstant;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.exception.ThrowUtils;
import com.markpen.library.model.dto.user.UserLoginRequest;
import com.markpen.library.model.dto.user.UserRegisterRequest;
import com.markpen.library.model.vo.UserVO;
import com.markpen.library.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMETER_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest==null, ErrorCode.PARAMETER_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        UserVO result = userService.login(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request){
        UserVO loginUser = userService.getLoginUser(request);
        return ResultUtils.success(loginUser);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMETER_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

}
