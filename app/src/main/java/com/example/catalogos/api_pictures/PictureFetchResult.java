package com.example.catalogos.api_pictures;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PictureFetchResult {
    @SerializedName("results")
    @Expose
    private ArrayList<PictureGoogle> results;

    public ArrayList<PictureGoogle> getResults() {
        return results;
    }
}