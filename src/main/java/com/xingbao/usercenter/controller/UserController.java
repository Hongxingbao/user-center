package com.xingbao.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xingbao.usercenter.constants.UserConstant;
import com.xingbao.usercenter.enumeration.ErrorCode;
import com.xingbao.usercenter.exception.BusinessException;
import com.xingbao.usercenter.pojo.dto.UserDeleteDTO;
import com.xingbao.usercenter.pojo.dto.UserLoginDTO;
import com.xingbao.usercenter.pojo.dto.UserRegisterDTO;
import com.xingbao.usercenter.pojo.entity.User;
import com.xingbao.usercenter.result.BaseResponse;
import com.xingbao.usercenter.result.ResultUtils;
import com.xingbao.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> UserRegister(@RequestBody UserRegisterDTO registerDTO) {
        long result = userService.userRegister(registerDTO.getUserAccount(), registerDTO.getUserPassword(), registerDTO.getCheckUserPassword());
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> UserLogin(@RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request) {
        User user = userService.userLogin(userLoginDTO.getUserAccount(), userLoginDTO.getUserPassword(), request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> UserLogout(HttpServletRequest request) {
        Integer result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getUserCurrent(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(UserConstant.USER_INFO);
        User currentUser = (User) obj;
        if(currentUser==null){
           throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long id = currentUser.getId();
        //当用户状态不为0,表示被封号或者账号注销状态
        if(currentUser.getUserStatus() != 0){
            throw new BusinessException(ErrorCode.BANNED);
        }
        User user = userService.getById(id);
        return ResultUtils.success(userService.getSafeUser(user));
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //没权限
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> list = userService.list(queryWrapper);
        List<User> userList = list.stream().map(user -> {
            user.setUserPassword(null);
            return user;
        }).collect(Collectors.toList());
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody UserDeleteDTO userDeleteDTO, HttpServletRequest request) {
        //没权限
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH);
        }
        Long id = userDeleteDTO.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 判断是否为管理员
     *
     * @param request
     * @return
     */
    private Boolean isAdmin(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(UserConstant.USER_INFO);
        User user = (User) obj;
        if (user == null || user.getUserRole() == UserConstant.DEFAULT_ROLE) {
            return false;
        }
        return true;
    }
}
