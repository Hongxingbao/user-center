package com.xingbao.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xingbao.usercenter.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 24840
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2023-09-07 11:50:10
* @Entity generator.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




