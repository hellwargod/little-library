package com.markpen.library.model.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3125L;

    private String userAccount;

    private String userPassword;
}
