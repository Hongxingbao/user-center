package com.xingbao.usercenter.pojo.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = -5788656379135379505L;
    private String userAccount;
    private String userPassword;
}
