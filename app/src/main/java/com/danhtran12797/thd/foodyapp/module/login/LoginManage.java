package com.danhtran12797.thd.foodyapp.module.login;

import com.danhtran12797.thd.foodyapp.model.JWTToken;
import com.danhtran12797.thd.foodyapp.service.APIService;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class LoginManage {

    ILogin loginListener;

    public LoginManage(ILogin loginListener) {
        this.loginListener = loginListener;
    }

    public void loginWithUserName(String username, String pass) {
        APIService.getService().LoginJWT(username, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jwtToken -> {
                    loginListener.setToken(jwtToken);
                    getUser(jwtToken);
                }, throwable -> {
                    loginListener.failure(throwable);
                });
    }

    public void loginWithSocial(String idUser, String name, String url) {
        APIService.getService().LoginSocial(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jwtToken -> {
                    loginListener.setToken(jwtToken);
                    getUser(jwtToken);
                }, throwable -> {
                    if (((HttpException) throwable).code() == 404) {
                        loginListener.register(idUser, name, url);
                    }
                });
    }

    private void getUser(JWTToken jwtToken) {
        APIService.getService().GetUser(jwtToken.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    loginListener.loginSuccess(users);
                }, throwable -> {
                    loginListener.failure(throwable);
                });
    }

}
