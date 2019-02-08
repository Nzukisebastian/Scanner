package com.example.jsons;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Qrcode {

    private String QR;
    @SerializedName("query")
    @Expose
    private String query;

    public Qrcode(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}