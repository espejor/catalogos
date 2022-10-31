package com.example.catalogos.services;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper {
   private static int BUFFER_SIZE = 6 * 1024;

   public static void zip(String[] files, String zipFile) throws IOException{
      BufferedInputStream origin = null;
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream (new FileOutputStream (zipFile)));
      try {
         byte data[] = new byte[BUFFER_SIZE];

         for (int i = 0; i < files.length; i++) {
            FileInputStream fi = new FileInputStream(files[i]);
            origin = new BufferedInputStream(fi, BUFFER_SIZE);
            try {
               ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
               out.putNextEntry(entry);
               int count;
               while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                  out.write(data, 0, count);
               }
            } finally {
               origin.close();
            }
         }
      } finally {
         out.close();
      }
   }

   public static void unzip(String zipFile, String location) throws IOException {
      try {
         File f = new File(location);
         if (!f.isDirectory()) {
            f.mkdirs();
         }
         ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
         try {
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
               String path = location + File.separator + ze.getName();

               if (ze.isDirectory()) {
                  File unzipFile = new File(path);
                  if (!unzipFile.isDirectory()) {
                     unzipFile.mkdirs();
                  }
               } else {
                  FileOutputStream fout = new FileOutputStream(path, false);

                  try {
                     for (int c = zin.read(); c != -1; c = zin.read()) {
                        fout.write(c);
                     }
                     zin.closeEntry();
                  } finally {
                     fout.close();
                  }
               }
            }
         } finally {
            zin.close();
         }
      } catch (Exception e) {
         e.printStackTrace();
         Log.e(TAG, "Unzip exception", e);
      }
   }

   public static void zipRecursively(String fullPathZipFileName, File fileToZip,String[] filesToNotInclude) throws Exception{
      FileOutputStream fos = new FileOutputStream(fullPathZipFileName);
      ZipOutputStream zos = new ZipOutputStream(fos);
      addDirToZipArchive(zos, fileToZip, null,filesToNotInclude);
      zos.flush();
      fos.flush();
      zos.close();
      fos.close();
   }

   private static void addDirToZipArchive(ZipOutputStream zos,File fileToZip, String parentDirectoryName,String[] filesToNotInclude) throws Exception {
      if (fileToZip == null || !fileToZip.exists()) {
         return;
      }

      String zipEntryName = fileToZip.getName();
      if (parentDirectoryName!=null && !parentDirectoryName.isEmpty()) {
         zipEntryName = parentDirectoryName + "/" + fileToZip.getName();
      }

      if (fileToZip.isDirectory()) {
         if(!Arrays.asList (filesToNotInclude).contains (fileToZip.getName ())) {
            System.out.println ("+" + zipEntryName);
            for (File file : fileToZip.listFiles ()) {
               addDirToZipArchive (zos, file, zipEntryName, filesToNotInclude);
            }
         }
      } else {
         System.out.println("   " + zipEntryName);
         byte[] buffer = new byte[1024];
         FileInputStream fis = new FileInputStream(fileToZip);
         zos.putNextEntry(new ZipEntry (zipEntryName));
         int length;
         while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
         }
         zos.closeEntry();
         fis.close();
      }
   }

}
