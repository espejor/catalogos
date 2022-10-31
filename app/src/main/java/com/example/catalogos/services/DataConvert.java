package com.example.catalogos.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.apache.commons.io.FilenameUtils;

import java.text.SimpleDateFormat;

public class DataConvert extends Service {

    public static SimpleDateFormat formatUS = new SimpleDateFormat ("yyyy-MM-dd");
    public static SimpleDateFormat formatSP = new SimpleDateFormat ("dd-MM-yyyy");

    public DataConvert(){

    }


//    public String cleanStringForSaving(String string){
//        if (string == null) {
//            return null;
//        }
//        if (isUriFromContent(string))
//            return string;
//        String fileName = FilenameUtils.getBaseName (string).replaceAll ("[^\\dA-Za-z]","");
//        String extension = FilenameUtils.getExtension (string);
//        return fileName + "." + extension;
//    }


    public boolean isUriFromMemory(String strURLAvatar){
        if(strURLAvatar != null)
            return strURLAvatar.indexOf ("content://") == 0;
        return false;
    }


    @Override
    public IBinder onBind(Intent intent){
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException ("Not yet implemented");
    }
}