package com.danhtran12797.thd.foodyapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListProvin {
    @SerializedName("LtsItem")
    @Expose
    private ArrayList<AddressDetail> ltsItem = null;

    public ArrayList<AddressDetail> getLtsItem() {
        return ltsItem;
    }
}
