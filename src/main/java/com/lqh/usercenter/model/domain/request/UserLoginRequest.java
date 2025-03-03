package com.lqh.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8299757898148794174L;

    private String userAccount;
    private String userPassword;
}
