package com.example.catalogos.google_drive_access.ui;

import static com.example.catalogos.google_drive_access.ui.BackupAppData.BACKUP_FILE_NAME;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class DriveServerHelper {
   private final Executor executor = Executors.newSingleThreadExecutor ();
   private final Context context;
   private final Drive mDriveService;
   SharedPreferences sharedPref;
   SharedPreferences.Editor editor;
   private final boolean updateDBOnGoogleDrive;
   String backupFileId;
   private String folderId;

   public DriveServerHelper(Context context, Drive mDriveService){
      this.context = context;
      this.mDriveService = mDriveService;
      sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
      editor = sharedPref.edit ();
      if(sharedPref.getString ("backupFileId", "").equals ("")) {
         editor.putString ("backupFileId", "");
         editor.putBoolean ("updateDB", true);
         editor.apply ();
      }

      backupFileId = sharedPref.getString ("backupFileId","");

      updateDBOnGoogleDrive = sharedPref.getBoolean ("updateDB",true);
   }



   public Task<String> getBackupFileFromGD(String fileBackupPath){

      return Tasks.call(executor,() -> {
         ByteArrayOutputStream byteArrayOutputStream = getFileFromGD();
         OutputStream outputStream = null;
         try {
            outputStream = new FileOutputStream (fileBackupPath);
            if (byteArrayOutputStream != null) {
               byteArrayOutputStream.writeTo (outputStream);
            }
         } catch (IOException e) {
            e.printStackTrace ();
         } finally {
            try {
               if (outputStream != null) {
                  outputStream.close ();
               }
            } catch (IOException e) {
               e.printStackTrace ();
            }
         }
         return fileBackupPath;
      });
   }

   private ByteArrayOutputStream getFileFromGD(){
      try {
         OutputStream outputStream = new ByteArrayOutputStream ();
         if(! backupFileId.equals (""))
            mDriveService.files ().get (backupFileId)
                 .executeMediaAndDownloadTo (outputStream);

         return (ByteArrayOutputStream) outputStream;
      } catch (GoogleJsonResponseException e) {
         // TODO(developer) - handle error appropriately
         System.err.println ("Unable to move file: " + e.getDetails ());
         return null;
      } catch (IOException e) {
         e.printStackTrace ();
         return null;
      }
   }

   public Task<String> createFileInGD(String filePath, String parentFolderId){
      return Tasks.call(executor,() -> {
         File apiFileMetaData = new File();
         apiFileMetaData.setName (BACKUP_FILE_NAME);
         java.io.File fileInDevice = new java.io.File (filePath);
         FileContent mediaContentToUpload = new FileContent ("multipart/form-data", fileInDevice);
         File apiFile = null;
         try {
            if (! backupFileId.equals ("") && updateDBOnGoogleDrive) {
               apiFile = mDriveService.files ().update (backupFileId,apiFileMetaData, mediaContentToUpload).execute ();
            }else{
               if(! parentFolderId.equals (""))
                  apiFileMetaData.setParents (Collections.singletonList (parentFolderId));

               apiFile = mDriveService.files ().create (apiFileMetaData, mediaContentToUpload).execute ();
               editor.putString ("backupFileId",apiFile.getId ());
               editor.apply();
            }
         }
         catch (UserRecoverableAuthIOException userRecoverableException) {
            ((Activity)context).startActivityForResult(
                    userRecoverableException.getIntent(),1000);
         }
         catch(GoogleJsonResponseException e){
            if(! parentFolderId.equals (""))
               apiFileMetaData.setParents (Collections.singletonList (parentFolderId));

            try {
               apiFile = mDriveService.files ().create (apiFileMetaData, mediaContentToUpload).execute ();
            } catch (IOException ex) {
               ex.printStackTrace ();
            }
            SharedPreferences.Editor editor = sharedPref.edit ();
            editor.putString ("backupFileId",apiFile.getId ());
            editor.apply();
         }
         catch (IOException e) {
            e.printStackTrace ();
         }
         catch (Exception e){
            throw  new Exception ("No sé qué ha pasado");
         }
         if (apiFile == null)
            throw new IOException ("Error en la creación de fichero.");

         return apiFile.getId ();
      });
   }

   public Task<String> createFolderInGD(String folderName,String parentFolderId){
      String folderPreferenceKey = "folder" + folderName + "Id";
      folderId = sharedPref.getString (folderPreferenceKey,"");
      return Tasks.call (executor, () -> {
         // File's metadata.
         File fileMetadata = new File ();
         fileMetadata.setName (folderName);
         fileMetadata.setMimeType ("application/vnd.google-apps.folder");
         File apiFolder = null;
         try {
            if (folderId.equals ("")) {
               if(! parentFolderId.equals (""))
                  fileMetadata.setParents (Collections.singletonList (parentFolderId));
               apiFolder = mDriveService.files ().create (fileMetadata).execute ();
               editor.putString (folderPreferenceKey,apiFolder.getId ());
               editor.apply();
            }
            else {
               apiFolder = existFileInGD(folderName,parentFolderId);
               if (apiFolder == null) {
                  if (! parentFolderId.equals (""))
                     fileMetadata.setParents (Collections.singletonList (parentFolderId));
                  apiFolder = mDriveService.files ().create (fileMetadata).execute ();
                  editor.putString (folderPreferenceKey, apiFolder.getId ());
                  editor.apply ();
               }
            }
         }
         catch (UserRecoverableAuthIOException userRecoverableException) {
            ((Activity)context).startActivityForResult(
                    userRecoverableException.getIntent(),1000);
         }// No se encuentra la carpeta -> la creamos
         catch (GoogleJsonResponseException e) {
            if(! parentFolderId.equals (""))
               fileMetadata.setParents (Collections.singletonList (parentFolderId));
            apiFolder = mDriveService.files ().create (fileMetadata).execute ();
            editor.putString (folderPreferenceKey,apiFolder.getId ());
            editor.apply();
         }
         catch (IOException e) {
            e.printStackTrace ();
         }
          if (apiFolder == null)
            throw new IOException ("Error en la creación de carpeta.");
         return apiFolder.getId ();
      });
   }

   private File existFileInGD(String folderName, String parentFolderId) throws IOException{
      String query = "name='" + folderName + "'";
      if (! parentFolderId.equals (""))
         query += " and parents in '" + parentFolderId + "'";
      Drive.Files.List listFiles = mDriveService.files().list()
              .setSpaces("drive")
              .setQ (query);
      FileList fileList = listFiles.execute();

      if(fileList.getFiles ().size () != 0)
         return fileList.getFiles ().get(0);
      else
         return null;
   }
}
