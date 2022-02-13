package com.danhtran12797.thd.foodyapp.model.zalopay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseCreateOrder {

    @SerializedName("zptranstoken")
    @Expose
    private String zptranstoken;
    @SerializedName("orderurl")
    @Expose
    private String orderurl;
    @SerializedName("returncode")
    @Expose
    private Integer returncode;
    @SerializedName("returnmessage")
    @Expose
    private String returnmessage;

    public String getZptranstoken() {
        return zptranstoken;
    }

    public void setZptranstoken(String zptranstoken) {
        this.zptranstoken = zptranstoken;
    }

    public String getOrderurl() {
        return orderurl;
    }

    public void setOrderurl(String orderurl) {
        this.orderurl = orderurl;
    }

    public Integer getReturncode() {
        return returncode;
    }

    public void setReturncode(Integer returncode) {
        this.returncode = returncode;
    }

    public String getReturnmessage() {
        return returnmessage;
    }

    public void setReturnmessage(String returnmessage) {
        this.returnmessage = returnmessage;
    }

}