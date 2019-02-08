package com.example.jsons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("confirmTicketPurchase")
    @Expose
    private ConfirmTicketPurchase confirmTicketPurchase;

    public ConfirmTicketPurchase getConfirmTicketPurchase() {
        return confirmTicketPurchase;
    }

    public void setConfirmTicketPurchase(ConfirmTicketPurchase confirmTicketPurchase) {
        this.confirmTicketPurchase = confirmTicketPurchase;
    }

}
