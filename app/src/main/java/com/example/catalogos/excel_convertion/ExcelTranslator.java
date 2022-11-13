package com.example.catalogos.excel_convertion;

import android.content.Context;
import android.database.Cursor;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;

public class ExcelTranslator {
   public static final String SEPARATOR = ";";
   Workbook workbook;
   private Context context;
   String excelPath;

   public ExcelTranslator(Context context){
      this.context = context;
      excelPath = context.getFilesDir () + "/tempExcel";
      //Crear libro de trabajo en blanco
      workbook = new HSSFWorkbook ();
   }

   public File translate(String fileName){

      File excelDir = new File(excelPath);
//      if (! excelDir.exists ())
      excelDir.mkdirs ();
      try {
         File file = new File (excelDir,fileName + ".xls");
         //Se genera el documento
         FileOutputStream out = new FileOutputStream(file);
         workbook.write(out);
         out.close();
         return file;
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   public ExcelTranslator sqlite2Excel(Cursor cursor){
      //Crea hoja nueva
      Sheet sheet = workbook.createSheet("Hoja de datos");
      int i = 0;
      Row row = sheet.createRow(i++);
      for (int j = 0; j < cursor.getColumnCount (); j++) {
         Cell cell = row.createCell(j);
         cell.setCellValue (cursor.getColumnName (j));
      }
      while (cursor.moveToNext()) {
         row = sheet.createRow(i++);
         for (int j = 0; j < cursor.getColumnCount (); j++) {
            Cell cell = row.createCell(j);
            switch (cursor.getType (j)) {
               case Cursor.FIELD_TYPE_NULL:
                  cell.setCellValue("");
                  break;
               case Cursor.FIELD_TYPE_INTEGER:
                  cell.setCellValue (cursor.getInt (j));
                  break;
               case Cursor.FIELD_TYPE_FLOAT:
                  cell.setCellValue (cursor.getFloat (j));
                  break;
               case Cursor.FIELD_TYPE_STRING:
                  cell.setCellValue (cursor.getString (j));
                  break;
               case Cursor.FIELD_TYPE_BLOB:
                  cell.setCellValue (cursor.getBlob (j).toString ());
                  break;
               default:
                  cell.setCellValue("");
            }
         }
      }
      return this;
   }
}
