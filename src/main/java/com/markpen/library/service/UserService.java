package com.markpen.library.service;

import com.markpen.library.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.markpen.library.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author markpen
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-06-12 15:00:03
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 检验密码
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);



    UserVO login(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 密码加密
     *
     * @param userPassword 用户密码
     * @return             加密后密码
     */
    String getEncryptPassword(String userPassword);

    /**
     * 获得脱敏后的登录用户信息
     *
     * @param user 用户信息
     * @return     脱敏后的登录用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    UserVO getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

}
