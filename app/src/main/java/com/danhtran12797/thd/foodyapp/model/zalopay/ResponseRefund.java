package com.danhtran12797.thd.foodyapp.model.zalopay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseRefund {

    @SerializedName("returncode")
    @Expose
    private Integer returncode;
    @SerializedName("returnmessage")
    @Expose
    private String returnmessage;
    @SerializedName("refundid")
    @Expose
    private String refundid;

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

    public String getRefundid() {
        return refundid;
    }

    public void setRefundid(String refundid) {
        this.refundid = refundid;
    }
}

