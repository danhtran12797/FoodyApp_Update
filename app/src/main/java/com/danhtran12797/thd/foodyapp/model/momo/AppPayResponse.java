package com.danhtran12797.thd.foodyapp.model.momo;

import com.google.gson.JsonObject;

public class AppPayResponse extends PayResponse {
    protected String transid;
    protected String message;

    public AppPayResponse(Integer status, String signature, Long amount, JsonObject error, String transid, String message) {
        super(status, signature, amount, error);
        this.transid = transid;
        this.message = message;
    }

    public String getTransid() {
        return transid;
    }

    public void setTransid(String transid) {
        this.transid = transid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
