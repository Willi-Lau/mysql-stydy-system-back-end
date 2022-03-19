package com.lwy.demo.service.impl;

import com.lwy.demo.TO.ResultDTO;
import com.lwy.demo.config.InfoConfig;
import com.lwy.demo.dao.LoginDao;
import com.lwy.demo.dao.RegisterDao;
import com.lwy.demo.entity.User;
import com.lwy.demo.service.RegisterService;
import com.lwy.demo.utils.RedisUtil;
import com.lwy.demo.utils.RedissionBloomFilters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

@Service
@Transactional
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RegisterDao registerDao;

    @Autowired
    private RedissionBloomFilters redissonBloomFilter;

    @Override
    public ResultDTO register(User user) throws Exception {
        ResultDTO resultDTO = new ResultDTO();
        //为空
        if(StringUtils.isEmpty(user.getClassName()) || StringUtils.isEmpty(user.getIdentityCardNumber()) ||
        StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getPhone()) ||
        StringUtils.isEmpty(user.getStudentNumber()) || StringUtils.isEmpty(user.getUniversity())
        ){
            resultDTO.setObject("信息不全已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
        //查询是否注册过  redis查询
        //查询学号
        StringBuilder studentNumberStringBuilder = new StringBuilder();
        studentNumberStringBuilder.append(InfoConfig.REDIS_USER_STUDENT_NUMBER);
        studentNumberStringBuilder.append(user.getStudentNumber());
        if(redisUtil.hasKey(studentNumberStringBuilder.toString())){
            resultDTO.setObject("此学号已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
        //查询身份证号
        StringBuilder identityCardNumberStringBuilder = new StringBuilder();
        identityCardNumberStringBuilder.append(InfoConfig.REDIS_USER_STUDENT_NUMBER);
        identityCardNumberStringBuilder.append(user.getStudentNumber());
        if(redisUtil.hasKey(identityCardNumberStringBuilder.toString())){
            resultDTO.setObject("此身份证号已注册");
            resultDTO.setType(false);
            return resultDTO;
        }
        //开始创建
        boolean registerMysqlResult = registerDao.register(user);
        if(!registerMysqlResult){
            resultDTO.setObject("超时，请重试");
            resultDTO.setType(false);
            return resultDTO;
        }
        //存入redis
        redisUtil.set(InfoConfig.REDIS_USER_STUDENT_NUMBER+user.getStudentNumber(),user.getPassword());
        redisUtil.set(InfoConfig.REDIS_USER_IDENTITY_CARD_NUMBER+user.getIdentityCardNumber(),user.getPassword());
        //存入bloom-filter
        redissonBloomFilter.addValue(user.getIdentityCardNumber());
        redissonBloomFilter.addValue(user.getStudentNumber());
        resultDTO.setType(true);
        return resultDTO;
    }
}
