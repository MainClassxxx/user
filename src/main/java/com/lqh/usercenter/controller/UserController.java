package com.lqh.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lqh.usercenter.common.BaseResponse;
import com.lqh.usercenter.common.ErrorCode;
import com.lqh.usercenter.common.ResultUtils;
import com.lqh.usercenter.exception.BusinessException;
import com.lqh.usercenter.model.domain.User;
import com.lqh.usercenter.model.domain.request.UserLoginRequest;
import com.lqh.usercenter.model.domain.request.UserRegisterRequest;
import com.lqh.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lqh.usercenter.contant.UserConstant.ADMIN_ROLW;
import static com.lqh.usercenter.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest ==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        String userAccount=userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword=userRegisterRequest.getCheckPassword();
        String planetCode=userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"参数含空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
//        return new BaseResponse<>(0,result,"ok");
        return ResultUtils.sucess(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if(userLoginRequest ==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        String userAccount=userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号和密码错误");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.sucess(user);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if(request ==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }

        int result = userService.userLogout(request);
        return ResultUtils.sucess(result);

    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;

        if (currentUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.sucess(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {

        //鉴权，仅管理员可进行查询
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        //java8
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.sucess(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"请求数据为空");
        }
        if(id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        boolean b = userService.removeById(id);
        return ResultUtils.sucess(b);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        //鉴权，仅管理员可进行查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;

        return user != null && user.getUserRole() == ADMIN_ROLW;
    }

}
