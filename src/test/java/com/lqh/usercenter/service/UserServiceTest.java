package com.lqh.usercenter.service;

import com.lqh.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.Assert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setUsername("tYupi");
        user.setUserAccount("dogKaipao");
        user.setAvatarUrl("https://baomidou.com/assets/asset.cIbiVTt_.svg");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = this.userService.save(user);
        System.out.println(user.getId());
        Assert.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "kaipao";
        String userPassword = "";
        String checkPassword = "123456";
        long userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,userRegister);
        userAccount="kai";
        userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,userRegister);
        userAccount = "kaipao";
        userPassword = "123456";
        userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,userRegister);
        userAccount = "kai pao";
        userPassword = "12345678";
        userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,userRegister);
        checkPassword = "123456789";
        userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,userRegister);
        userAccount = "dogKaipao";
        checkPassword = "12345678";
        userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1,userRegister);
        userAccount = "kaipao";
        userRegister = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertTrue(userRegister > 0);
    }
}