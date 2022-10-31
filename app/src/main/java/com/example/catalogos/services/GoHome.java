package com.example.catalogos.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.catalogos.init_package.MainActivity;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;

public class GoHome {
    private  Activity activity;

    public GoHome(Activity activity){
        this.activity = activity;
    }

    public void execute(){
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
}
