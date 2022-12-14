package com.example.catalogos.google_drive_access.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.catalogos.dialog_package.AlertDialogConfirmation;
import com.example.catalogos.services.Zipper;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class to demonstrate use-case of create file in the application data folder.
 */
public class BackupAppData {

   private static final String FOLDER_FOR_APP_IN_GD = "Catalogos Backup";
   public static final String BACKUP_FILE_NAME = "catalogo.zip";
   private Context context;
   private GoogleSignInAccount account;
   private DriveServerHelper driveServiceHelper;

   public String sdkDir;   // Path del directorio de Datos de la app
   private final String backupDir;  // Path del directorio de la carpeta de backup
   private final String filePathBackUp;   //Path del archivo de backup .zip

   public BackupAppData(Context context, GoogleSignInAccount account){

      this.context = context;
      this.account = account;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         sdkDir = context.getDataDir().toString ();
      }else{
         sdkDir = Objects.requireNonNull (context.getFilesDir ().getParentFile ()).toString ();
//                 "/data/user/0/com.example.catalogos";
      }
      backupDir = sdkDir + "/backup";

      filePathBackUp = backupDir + "/" + BACKUP_FILE_NAME;
   }

   /**
    * Creates a file in the application data folder.
    *
    * @return Created file's Id.
    */
   public void uploadBackupAppData() throws IOException {
        /*Load pre-authorized user credentials from the environment.
        TODO(developer) - See https://developers.google.com/identity for
        guides on implementing OAuth2 for your application.*/
      GoogleAccountCredential credentials = null;
         credentials = GoogleAccountCredential
                 .usingOAuth2 (context,Collections.singleton (DriveScopes.DRIVE_FILE));
         credentials.setSelectedAccount (account.getAccount ());
         builtAuthorizedAPIClientservice(credentials);

      String[] filesToNotInclude = new String[]{
         "backup", "cache","code_cache","no_backup","shared_prefs","lib"
      };
      packToZip (sdkDir,filesToNotInclude);
         // Creamos la Carpeta donde se guardar?? la copia de los archivos de la app
      String folderId = createRootFolderInGD (FOLDER_FOR_APP_IN_GD,"");
   }

   private void packToZip(String filePath, String[] filesToNotInclude){
      ProgressDialog progressDialog = new ProgressDialog (this.context);
      progressDialog.setTitle ("Comprimiendo archivos");
      progressDialog.setMessage ("Espere...");
      progressDialog.show ();
      final File backupDBFolder = new File(backupDir);
      backupDBFolder.mkdirs();
      try {
         Zipper.zipRecursively(filePathBackUp,new File(filePath),filesToNotInclude);
      } catch (Exception e) {
         e.printStackTrace ();
      }
      progressDialog.dismiss ();

   }


   private String createRootFolderInGD(String folderName, String parentFolderId){
      final String[] id = new String[1];
      driveServiceHelper.createFolderInGD (folderName,parentFolderId)
         .addOnSuccessListener(new OnSuccessListener<String> () {
            @Override
            public void onSuccess(String folderId){
               uploadFilesToGD (folderId);
               id[0] = folderId;
            }
         })
        .addOnFailureListener (new OnFailureListener () {
           @Override
           public void onFailure(@NonNull Exception e){

           }
        });
      return id[0];
   }

   private void builtAuthorizedAPIClientservice(GoogleAccountCredential credentials) throws IOException{

      // Build a new authorized API client service.
      Drive googleDriveService = new Drive.Builder(new NetHttpTransport(),
              JacksonFactory.getDefaultInstance(),
              credentials)
              .setApplicationName("Catalogos")
              .build();
      
      driveServiceHelper = new DriveServerHelper (context,googleDriveService);
   }

   public void uploadFilesToGD(String parentFolderId){
      ProgressDialog progressDialog = new ProgressDialog (context);
      progressDialog.setTitle ("Subiendo DB a Google Drive");
      progressDialog.setMessage ("Espere...");
      progressDialog.show ();

      driveServiceHelper.createFileInGD (BackupAppData.this.filePathBackUp,parentFolderId)
         .addOnSuccessListener (new OnSuccessListener<String> () {
            @Override
            public void onSuccess(String s){
               progressDialog.dismiss ();
               Toast.makeText (context, "La BD se ha guardado en Google Drive", Toast.LENGTH_LONG).show ();
            }
         }).addOnFailureListener (new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e){
               progressDialog.dismiss ();
               Toast.makeText (context, "Ha habido un error.\nNo se ha guardado la BD", Toast.LENGTH_LONG).show ();
            }
         });
   }

   public void downloadAppData(){
      try {
         Method onConfirm = this.getClass ().getMethod ("downloadData", (Class<?>[]) null);
         new AlertDialogConfirmation (context, this, "Se va a descargar una copia de seguridad\n??Ojo! Se van a sobreescribir los datos actuales con los de la ??ltima copia guardada\n??Est?? de acuerdo?", onConfirm, null);
      } catch (NoSuchMethodException e) {
         e.printStackTrace ();
      }
   }

   public void downloadData() throws IOException{
      GoogleAccountCredential credentials = null;
      credentials = GoogleAccountCredential
              .usingOAuth2 (context,Collections.singleton (DriveScopes.DRIVE_FILE));
      credentials.setSelectedAccount (account.getAccount ());
      builtAuthorizedAPIClientservice(credentials);

      ProgressDialog progressDialog = new ProgressDialog (context);
      progressDialog.setTitle ("Descargando Copia de seguridad desde Google Drive");
      progressDialog.setMessage ("Espere...");
      progressDialog.show ();

      // Descargar copia desde GD
      driveServiceHelper.getBackupFileFromGD (filePathBackUp)
        .addOnSuccessListener (new OnSuccessListener<String> () {
           @Override
           public void onSuccess(String s){
              // Descomprimir el archivo
              try {
                 unpackZipFile ();
              } catch (Exception e) {
                 progressDialog.dismiss ();
                 Toast.makeText (context, "Ha habido un error.\nNo se ha descargado la Copia", Toast.LENGTH_LONG).show ();
                 e.printStackTrace ();
              }
              progressDialog.dismiss ();
              Toast.makeText (context, "Se ha descargado la copia de seguridad", Toast.LENGTH_LONG).show ();
           }
        })
        .addOnFailureListener (new OnFailureListener () {
           @Override
           public void onFailure(@NonNull Exception e){
              progressDialog.dismiss ();
              Toast.makeText (context, "Ha habido un error.\nNo se ha descargado la Copia", Toast.LENGTH_LONG).show ();
           }
        });
   }

   private void unpackZipFile() throws Exception{
      unzipFileIntoDirectory (new File (filePathBackUp), new File (sdkDir).getParentFile ());
   }
   public void unzipFileIntoDirectory(File archive, File destinationDir)
           throws Exception {
      final int BUFFER_SIZE = 1024;
      BufferedOutputStream dest = null;
      FileInputStream fis = new FileInputStream(archive);
      ZipInputStream zis = new ZipInputStream(new BufferedInputStream (fis));
      ZipEntry entry;
      File destFile;
      while ((entry = zis.getNextEntry()) != null) {
         destFile = new File(destinationDir, entry.getName());
         if (entry.isDirectory()) {
            destFile.mkdirs();
            continue;
         } else {
            int count;
            byte data[] = new byte[BUFFER_SIZE];
            destFile.getParentFile().mkdirs();
            boolean deleted = false;
            if(destFile.exists ())
               deleted = destFile.delete ();
            FileOutputStream fos = new FileOutputStream(destFile);
            dest = new BufferedOutputStream(fos, BUFFER_SIZE);
            while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
               dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
            fos.close();
         }
      }
      zis.close();
      fis.close();
   }
}
