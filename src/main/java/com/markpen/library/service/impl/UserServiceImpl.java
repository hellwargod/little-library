package com.markpen.library.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markpen.library.exception.BusinessException;
import com.markpen.library.exception.ErrorCode;
import com.markpen.library.model.entity.User;
import com.markpen.library.mapper.UserMapper;
import com.markpen.library.model.enums.UserRoleEnum;
import com.markpen.library.model.vo.UserVO;
import com.markpen.library.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static com.markpen.library.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author markpen
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-06-12 15:00:03
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 检查参数
        if(StrUtil.hasBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"参数为空");
        }
        if(userAccount.length()<6){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"账号过短");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"密码过短");
        }
        if(!checkPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"两次密码输入不一致");
        }

        // 检查是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if(count>0){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"账号已存在");
        }

        // 对密码进行加密
        String encryptPassword = getEncryptPassword(userPassword);

        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName("None");
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if(!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"注册失败，数据库系统错误");
        }
        return user.getId();
    }

    @Override
    public UserVO login(String userAccount, String userPassword, HttpServletRequest request) {
        // 检查参数
        if(StrUtil.hasBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"参数为空");
        }
        if(userAccount.length()<6){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"账号过短");
        }
        if(userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"密码过短");
        }

        // 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", getEncryptPassword(userPassword));
        User user = this.baseMapper.selectOne(queryWrapper);
        if(user==null){
            log.info("user login failed");
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"用户不存在或密码错误");
        }

        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return this.getUserVO(user);
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "songchengcheng";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public UserVO getUserVO(User user) {
        if(user==null){
            return null;
        }
        UserVO loginUserVO = new UserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        // 判断是否登录，即Session中是否有用户信息并已登录（对于cookie的）
        Object obj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) obj;
        if(currentUser==null || currentUser.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"请先登录");
        }
        // 从数据库中查询
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if(currentUser==null || currentUser.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"请先登录");
        }

        return this.getUserVO(currentUser);
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }
}




