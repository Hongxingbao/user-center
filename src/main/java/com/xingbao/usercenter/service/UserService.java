package com.xingbao.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xingbao.usercenter.pojo.dto.UserRegisterDTO;
import com.xingbao.usercenter.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 24840
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-09-07 11:50:10
*/
public interface UserService extends IService<User> {

    /**
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkUserPassword 检验密码
     * @return
     */
    long userRegister(String userAccount,String userPassword, String checkUserPassword);

    /**
     * 登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 脱敏
     * @param user
     * @return
     */
    public User getSafeUser(User user);

    /**
     * 注销
     * @param request
     * @return
     */
    public Integer userLogout(HttpServletRequest request);


}
