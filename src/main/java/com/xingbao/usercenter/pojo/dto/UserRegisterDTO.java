package com.xingbao.usercenter.pojo.dto;

import lombok.Data;


import java.io.Serializable;

@Data
public class UserRegisterDTO implements Serializable {

    private static final long serialVersionUID = -6599472960686725793L;
    private String userAccount;
    private String userPassword;
    private String checkUserPassword;
}
