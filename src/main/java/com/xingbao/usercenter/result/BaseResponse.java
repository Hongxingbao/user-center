package com.xingbao.usercenter.result;

import com.xingbao.usercenter.enumeration.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private Integer code;
    private T data;
    private String msg;
    private String description;

    public BaseResponse(Integer code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }

    public BaseResponse(Integer code, T data, String msg) {
        this(code,data,msg,"");
    }

    public BaseResponse(Integer code, T data) {
        this(code,data,"","");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
}
