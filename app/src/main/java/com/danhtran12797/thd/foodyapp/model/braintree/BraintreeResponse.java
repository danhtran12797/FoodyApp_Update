package com.danhtran12797.thd.foodyapp.model.braintree;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BraintreeResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("transaction")
    @Expose
    private Transaction transaction;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
