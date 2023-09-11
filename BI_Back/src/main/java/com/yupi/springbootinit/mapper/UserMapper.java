package com.yupi.springbootinit.mapper;

import com.yupi.springbootinit.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 叶孙勇
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2023-08-28 15:53:16
* @Entity com.yupi.springbootinit.model.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

}




