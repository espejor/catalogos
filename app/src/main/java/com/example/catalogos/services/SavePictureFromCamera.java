package com.example.catalogos.services;

import android.graphics.Bitmap;

import com.example.catalogos.xyz.App;


public class SavePictureFromCamera {
    private final Bitmap bitmap;
    String folder;
    ImageSaver imageSaver = new ImageSaver (App.getContext());
    String name,fileType;


    public SavePictureFromCamera(Bitmap bitmap, String fileName, String folderName){
        name = fileName;
        folder = folderName == null?"":folderName;
//        fileType = "jpg";
        this.bitmap = bitmap;
    }

    public void save(){
        imageSaver.setDirectory (folder).setFileName (name).save (bitmap);
    }
}

