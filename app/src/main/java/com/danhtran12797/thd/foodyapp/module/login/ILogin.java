package com.danhtran12797.thd.foodyapp.module.login;

import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.model.User;

import java.util.List;

public interface ILogin {
    void loginSuccess(List<User> users);
    void register(String idUser, String name, String url);
    void setToken(JWTToken token);
    void failure(Throwable throwable);
}
