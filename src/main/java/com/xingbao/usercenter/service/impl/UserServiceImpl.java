package com.xingbao.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xingbao.usercenter.constants.UserConstant;
import com.xingbao.usercenter.enumeration.ErrorCode;
import com.xingbao.usercenter.exception.BusinessException;
import com.xingbao.usercenter.mapper.UserMapper;
import com.xingbao.usercenter.pojo.entity.User;
import com.xingbao.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.regex.Pattern;


/**
 * @author 24840
 * @description 针对表【user(用号信息)】的数据库操作Service实现
 * @createDate 2023-09-07 11:50:10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * @param userAccount       用号账号
     * @param userPassword      用号密码
     * @param checkUserPassword 检验密码
     * @return
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkUserPassword) {

        //1.校验用号的账号、密码、校验密码，是否符合要求
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkUserPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        // 账号长度 不小于 4 位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于四位");
        }
        // 密码不小于 8 位
        if (userPassword.length() < 8 || checkUserPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码小于8位");
        }
        // 账号不包含特殊字符
        String regex = "^[a-zA-Z0-9_]+$";
        Pattern pattern = Pattern.compile(regex);
        boolean isMatch = pattern.matcher(userAccount).matches();
        if (!isMatch) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkUserPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码和校验密码不一致");
        }
        //  账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已存在");
        }

        // 2.对密码进行加密(密码千万不要直接以明文存储到数据库中)
        String finalPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());
        User finalUser = new User();
        //脱敏
       finalUser.setId(0L);
       finalUser.setUsername("");
       finalUser.setUserAccount(userAccount);
       finalUser.setAvatarUrl("");
       finalUser.setGender(0);
       finalUser.setUserPassword(finalPassword);
       finalUser.setPhone("");
       finalUser.setEmail("");
       finalUser.setUserStatus(0);
       finalUser.setCreateTime(new Date());
       finalUser.setUpdateTime(new Date());
       finalUser.setIsDelete(0);
       finalUser.setUserRole(0);

        //3.向数据库插入用号数据
        boolean result = this.save(finalUser);
        if (!result) {
            return -1;
        }
        return finalUser.getId();
    }

    /**
     * 登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验用户账号和密码是否合法
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR,"参数为空");
        }
        // 账号长度 不小于 4 位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于四位");
        }
        // 密码不小于 8 位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码小于8位");
        }
        // 账号不包含特殊字符
        String regex = "^[a-zA-Z0-9_]+$";
        Pattern pattern = Pattern.compile(regex);
        boolean isMatch = pattern.matcher(userAccount).matches();
        if (!isMatch) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //校验用户密码是否输入正确
        String finalPassword = DigestUtils.md5DigestAsHex((UserConstant.SALT + userPassword).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", finalPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("Login failed, wrong username or password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"错误的用户名或密码");
        }
        //脱敏
        User finalUser = getSafeUser(user);
        request.getSession().setAttribute(UserConstant.USER_INFO, finalUser);
        return finalUser;
    }

    @Override
    public Integer userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_INFO);
        return 0;
    }

    /**
     * 脱敏
     * @param user
     * @return
     */
    public User getSafeUser(User user) {
        User finalUser = new User();
        finalUser.setId(user.getId());
        finalUser.setUsername(user.getUsername());
        finalUser.setUserAccount(user.getUserAccount());
        finalUser.setAvatarUrl(user.getAvatarUrl());
        finalUser.setGender(user.getGender());
        finalUser.setPhone(user.getPhone());
        finalUser.setEmail(user.getEmail());
        finalUser.setUserStatus(user.getUserStatus());
        finalUser.setCreateTime(user.getCreateTime());
        finalUser.setUserRole(user.getUserRole());
        return finalUser;
    }
}




