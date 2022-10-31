package com.example.catalogos.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.catalogos.xyz.App;

import org.apache.commons.io.FilenameUtils;

public class SavePictureFromURI extends AsyncTask<Void, Void, Bitmap> {
    private Bitmap bitmap;
    private Uri uriURL;
    String folder;
    ImageSaver imageSaver = new ImageSaver (App.getContext());
    String strURL,name,fileType;


    public SavePictureFromURI(String url, String fileName, String folderName){
        strURL = url;
        name = fileName;
        folder = folderName == null?"":folderName;
        fileType = FilenameUtils.getExtension (strURL);
    }
    public SavePictureFromURI(Bitmap bitmap, String fileName, String folderName){
        this.bitmap = bitmap;
        name = fileName;
        folder = folderName == null?"":folderName;
        fileType = "jpg";
    }
    @Override
    protected Bitmap doInBackground(Void... voids){
        if(bitmap != null)
            return bitmap;
        return  imageSaver.makeBitmap (strURL);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        super.onPostExecute (bitmap);
        // Guardar en la carpeta de assests
//            folder = "assets/auctions_house";
//            String nameFile = name;
        imageSaver.setDirectory (folder).setFileName (name + "." + fileType).save (bitmap);
    }
}

