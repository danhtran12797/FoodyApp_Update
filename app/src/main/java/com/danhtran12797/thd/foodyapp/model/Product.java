package com.danhtran12797.thd.foodyapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Product implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("compo")
    @Expose
    private String compo;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sale1")
    @Expose
    private String sale1;
    @SerializedName("sale2")
    @Expose
    private String sale2;
    @SerializedName("idCategory")
    @Expose
    private String idCategory;
    @SerializedName("countLove")
    @Expose
    private String countLove;

    public String getCountLove() {
        return countLove;
    }

    public void setCountLove(String countLove) {
        this.countLove = countLove;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompo() {
        return compo;
    }

    public void setCompo(String compo) {
        this.compo = compo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSale1() {
        return sale1;
    }

    public void setSale1(String sale1) {
        this.sale1 = sale1;
    }

    public String getSale2() {
        return sale2;
    }

    public void setSale2(String sale2) {
        this.sale2 = sale2;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

}