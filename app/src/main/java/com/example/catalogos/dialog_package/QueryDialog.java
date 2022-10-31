package com.example.catalogos.dialog_package;

import android.content.Context;

import java.lang.reflect.Method;

public class QueryDialog extends AlertDialogConfirmation {
   public QueryDialog(Context context, Object obj, String message, Method onConfirm, Method onReject){
      super (context, obj, message, onConfirm, onReject);
      setTitle ("Pregunta...");
   }
}
