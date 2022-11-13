package com.example.catalogos.dialog_package;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;

import com.example.catalogos.R;
import com.example.catalogos.lots_package.lots.JewelsByLotDetailFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AlertDialogConfirmation {

//    private String title;
//    private Method onConfirm;
//    private Method onReject;
    protected AlertDialog.Builder alert;


    public AlertDialogConfirmation(Context context, Object obj, String message, Method onConfirm, Method onReject){
//        this.title = title;
//        this.onConfirm = onConfirm;
//        this.onReject = onReject;
        alert = new AlertDialog.Builder (context);
        alert.setTitle ("¡Atención!").setIcon(R.drawable.ic_baseline_warning_24).setMessage (message);
        alert.setPositiveButton ("Sí", new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int whichButton){
                try {
                    if(onConfirm != null)
                        onConfirm.invoke (obj, (Object[]) null);
                } catch (IllegalAccessException  e) {
                    e.printStackTrace ();
                } catch (InvocationTargetException e) {
//                    e.printStackTrace ();
                }
            }
        });


        alert.setNegativeButton ("No", new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int whichButton){
                try {
                    if(onReject != null)
                        onReject.invoke (obj, (Object[]) null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace ();
                } catch (InvocationTargetException e) {
                    e.printStackTrace ();
                }

            }
        });
        alert.show ();
    }

    public void setTitle(String title){
        alert.setTitle (title);
    }

}