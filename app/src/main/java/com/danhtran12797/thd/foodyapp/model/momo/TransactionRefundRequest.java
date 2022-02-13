package com.danhtran12797.thd.foodyapp.model.momo;

public class TransactionRefundRequest{
    private String requestId;
    private String hash;
    Double version;
    String partnerCode;

    public TransactionRefundRequest(String partnerCode, Double version, String requestId, String hash) {
        this.partnerCode = partnerCode;
        this.hash = hash;
        this.requestId=requestId;
        this.version=version;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }
}