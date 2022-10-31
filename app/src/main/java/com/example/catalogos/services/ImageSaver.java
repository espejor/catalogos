package com.example.catalogos.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.catalogos.xyz.App;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class ImageSaver {

    public static final String FOLDER_PATH = App.getContext ().getFilesDir().getAbsolutePath();

    private String directoryName = "images";
    private String fileName = "image.png";
    private Context context;
    private File dir;
    private boolean external = false;

    public ImageSaver(Context context){
        this.context = context;
    }

    public ImageSaver setFileName(String fileName){
        this.fileName = fileName;
//        fileName = FilenameUtils.getBaseName (fileName).replaceAll ("[^\\dA-Za-z]","");
//        String extension = FilenameUtils.getExtension (fileName);
//        this.fileName = fileName + "." + extension;
        return this;
    }

    public ImageSaver setExternal(boolean external){
        this.external = external;
        return this;
    }

    public ImageSaver setDirectory(String directoryName){
        this.directoryName = directoryName != ""?directoryName:this.directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage){
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream (createFile ());
            bitmapImage.compress (Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close ();
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

    @NonNull
    private File createFile(){
        File directory;
        boolean success = true;
        if (external) {
            directory = getAlbumStorageDir (directoryName);
            if (! directory.exists ()) {
                directory.mkdir ();
            }
        } else {
            directory = new File (context.getFilesDir () + "/" + directoryName);
            if (! directory.exists ()) {
                success = directory.mkdirs ();
            }
        }
        Log.e ("Crear directorio",String.valueOf (success));
        return new File (directory, fileName);
    }

    private File getAlbumStorageDir(String albumName){
        File file = new File (Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES), albumName);
        if (! file.mkdirs ()) {
            Log.e ("ImageSaver", "Directory not created");
        }
        return file;
    }

    public static boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState ();
        return Environment.MEDIA_MOUNTED.equals (state);
    }

    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState ();
        return Environment.MEDIA_MOUNTED.equals (state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals (state);
    }

    public Bitmap load(){
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream (createFile ());
            return BitmapFactory.decodeStream (inputStream);
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close ();
                }
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
        return null;
    }

    public boolean deleteFile(){
        File file = createFile ();
        return file.delete ();
    }

    public boolean renameFile(String newFileName){
        File oldFile = createFile ();
        fileName = newFileName;
        File newFile = createFile ();
        return oldFile.renameTo (newFile);
    }

    public Bitmap makeBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap =  MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return bitmap;
    }
    public Bitmap makeBitmap(String link){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream (new URL (link).openConnection ().getInputStream ());
        } catch (IOException e) {
            e.printStackTrace ();
        }
        return bitmap;
    }

}