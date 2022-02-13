package com.danhtran12797.thd.foodyapp.model;

import java.io.Serializable;

public class AddressShipping implements Serializable {
    private String name;
    private String address;
    private String phone;
    private boolean check;

    public AddressShipping(String name, String address, String phone, boolean check) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.check = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
