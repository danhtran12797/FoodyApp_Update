package com.danhtran12797.thd.foodyapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {

    @SerializedName("idBanner")
    @Expose
    private String idBanner;
    @SerializedName("nameBanner")
    @Expose
    private String nameBanner;
    @SerializedName("titleBanner")
    @Expose
    private String titleBanner;
    @SerializedName("imageBanner")
    @Expose
    private String imageBanner;
    @SerializedName("idProduct")
    @Expose
    private String idProduct;

    public String getIdBanner() {
        return idBanner;
    }

    public void setIdBanner(String idBanner) {
        this.idBanner = idBanner;
    }

    public String getNameBanner() {
        return nameBanner;
    }

    public void setNameBanner(String nameBanner) {
        this.nameBanner = nameBanner;
    }

    public String getTitleBanner() {
        return titleBanner;
    }

    public void setTitleBanner(String titleBanner) {
        this.titleBanner = titleBanner;
    }

    public String getImageBanner() {
        return imageBanner;
    }

    public void setImageBanner(String imageBanner) {
        this.imageBanner = imageBanner;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

}