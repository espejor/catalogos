package com.example.catalogos.services;

import com.example.catalogos.api_pictures.PictureGoogle;

import  retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleAPIService {

    @GET("/customsearch/v1")
    Call<PictureGoogle> getPictures(
        @Query("q") String textToSearch,
        @Query("key") String googleKey,
        @Query("cx") String cx,
//        @Query("imgType") String imgType,
        @Query("searchType") String searchType,
        @Query("fileType") String fileType,
        @Query("imgSize") String imgSize,
        @Query ("start") int start
    );
}