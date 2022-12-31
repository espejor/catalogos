package com.example.catalogos.pdf_creator;


import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;
import static com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import static com.itextpdf.text.FontFactory.HELVETICA;
import static com.itextpdf.text.FontFactory.HELVETICA_BOLD;
import static com.itextpdf.text.FontFactory.HELVETICA_OBLIQUE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.AuctionEntry;
import com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class PDFCreator {
    public static final int NORMAL = 10;
    public static final BaseColor LIGHT_GRAY_WATERMARK = new BaseColor(192, 192, 192, 127);
    public static final BaseColor BACKGROUND_VERY_LIGHT_GRAY = new BaseColor(220,220,220);

    private final Rectangle pageSize = PageSize.A4.rotate ();

    private final Context context;
    private final String fileName;
    File file;
    String pdfPath;
    private Cursor cursor = null;
    int columnsList = 9;
    private Document document;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String title;
    private String owner;
    private String waterMark;

    private Phrase txtTitle;
    private Phrase txtOwner;
    private Phrase txtWaterMark;

    private PdfPCell currentOwnerCell;
    private PdfPCell currentHallmarkCell;
    private PdfPCell currentGemstoneCell;
    private boolean currentOwnerCellPrinted = true;
    private boolean currentHallmarkCellPrinted = true;
    private boolean currentGemstoneCellPrinted = true;
    
    List ownersList = new List();
    List hallmarksList = new List();
    List gemstonesList = new List();

    private Font fBold16;
    private Font fBold24;
    private Font fBold42WM;
    private Font fNormal;
    private Font fItalic;
    private Uri uri;
    private String oldOwner;
    private String oldHallmark;
    private ArrayList<String[]> gemstonesPrinted = new ArrayList<> ();

    public PDFCreator(Context context,String filename){
        if (Build.VERSION.SDK_INT >= 22)
            verifyStoragePermissions(context);

        this.context = context;
        this.fileName = filename + ".pdf";
        settingFonts();
        createPDF();
    }

    public String getFilePath(){
        return file.getAbsolutePath ();
    }

    private void settingFonts(){

        fBold16 = FontFactory.getFont (HELVETICA_BOLD,16);
        fBold24 = FontFactory.getFont (HELVETICA_BOLD,24);
        fBold42WM = FontFactory.getFont (HELVETICA_BOLD,42,Font.ITALIC,LIGHT_GRAY_WATERMARK);
        fNormal = FontFactory.getFont(HELVETICA,NORMAL);
        fItalic = FontFactory.getFont(HELVETICA_OBLIQUE,NORMAL);
    }

    public PDFCreator(Context context){
        this (context,"catalogoTemp");
    }

    private void createPDF(){
        pdfPath = context.getFilesDir () + "/tempPDF";

        File pdfDir = new File(pdfPath);
        if (! pdfDir.exists ())
            pdfDir.mkdirs ();
        file = new File(pdfDir,fileName);
        uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        try {
            document = new Document(pageSize);// Landscape
            PdfWriter.getInstance (document,new FileOutputStream (getFilePath ()))
                    .setPageEvent(new MyPdfPageEventHelper());
            document.open ();
        } catch (DocumentException | IOException e) {
            e.printStackTrace ();
        }

    }
    private Locale setLocale(Locale desiredLocale){
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;
        conf.locale = desiredLocale; // whatever you want here
        res.updateConfiguration(conf, null); // second arg null means don't change
        return savedLocale;
    }

    public void createListOfImagesPDF(Cursor cursor) throws DocumentException, IOException{
        this.cursor = cursor;
        int columns = 6;

        int[] widths = new int[columns];
        for (int i = 0; i < columns; i++) {
            widths[i] = 1;
        }

        // agregar el título
        Locale oldLocale = setLocale (Locale.ENGLISH);
        addTitlePage ();
        setLocale (oldLocale);

        // Adding paragraphs to document
        PdfPTable table = new PdfPTable (columns);
        table.setWidthPercentage (100);
        table.setWidths (widths);


        // Recorrer el cursor para imprimir todas las joyas
        cursor.moveToFirst ();
        for (int i = 0; i < cursor.getCount (); i++) {
            cursor.moveToPosition (i);

            String entryAvatar =  JewelEntry.AVATAR_URI;
            int column = cursor.getColumnIndex(entryAvatar);
            String avatarUri = cursor.getString(column);
            String uri = JEWEL_FILE_PATH + avatarUri;
            Image image = null;
            if (avatarUri != null){
                // Creating an ImageData object
                image = Image.getInstance (uri);
                if(image.getHeight () < image.getWidth ()) {
//                    image.scaleAbsolute (60F, 40F);
                    image.setRotationDegrees (90);
                }
            }
            PdfPCell c = new PdfPCell (image,true);

            c.setPadding (4F);
            c.setBorder (15);
            table.addCell (c);
        }

        table.completeRow();
        document.add (table);
        document.add(Chunk.NEXTPAGE);

        document.close();

        Toast.makeText(context, "El documento se ha creado correctamente", Toast.LENGTH_SHORT).show();

    }


    public void createListPDF(Cursor cursor) throws DocumentException{
        this.cursor = cursor;

        // agregar el título
        Locale oldLocale = setLocale (Locale.ENGLISH);
        addTitlePage ();

        PdfPTable table = new PdfPTable(columnsList);
        table.setWidthPercentage (100);

        // Lista
        //Orden de los cortes
        //fkAuctionId, lot,jewelFullDataView.id , owner,gemstone,cut
        // Control de cortes de bucle
        String oldAuction = "";
        String oldLot = "";
        String oldJewelId = "";

        // Recorrer el cursor para imprimir todas las joyas
        for (int i = 0; i < cursor.getCount (); i++) {
            cursor.moveToPosition (i);

            // Get valores.
            String entryAuction =  AuctionEntry.NAME;
            String entryLot =  JewelEntry.LOT;
            String entryJewelId = JewelEntry.ID;
            String entryOwner =  OwnerEntry.NAME;
            String entryHallmark =  HallmarkEntry.NAME;


            int column;
            column = cursor.getColumnIndex(entryAuction);
            String auction = cursor.getString(column);
            column = cursor.getColumnIndex(entryLot);
            String lot = cursor.getString(column);
            column = cursor.getColumnIndex(entryJewelId);
            String jewelId = cursor.getString(column);
            column = cursor.getColumnIndex(entryOwner);
            String owner = cursor.getString(column);
            column = cursor.getColumnIndex(entryHallmark);
            String hallmark = cursor.getString(column);
//            column = cursor.getColumnIndex(entryGemstone);
//            String gemstone = cursor.getString(column);
//            column = cursor.getColumnIndex(entryCut);
//            String cut = cursor.getString(column);

            if(! auction.equals (oldAuction)){// Cambio de Subasta
                if (!currentOwnerCellPrinted) {
                    table.addCell (currentOwnerCell);
                    currentOwnerCellPrinted = true;
                }
                if (!currentHallmarkCellPrinted) {
                    table.addCell (currentHallmarkCell);
                    currentHallmarkCellPrinted = true;
                }
                if (!currentGemstoneCellPrinted) {
                    table.addCell (currentGemstoneCell);
                    currentGemstoneCellPrinted = true;
                }
                printAuction(cursor, table);
                oldAuction = auction;
                oldLot = "";
                oldJewelId = "";
                oldOwner = "";
                oldHallmark = "";
            }
            if(! lot.equals (oldLot)){  // Cambio de lote
                if (!currentOwnerCellPrinted) {
                    table.addCell (currentOwnerCell);
                    currentOwnerCellPrinted = true;
                }
                if (!currentHallmarkCellPrinted) {
                    table.addCell (currentHallmarkCell);
                    currentHallmarkCellPrinted = true;
                }
                if (!currentGemstoneCellPrinted) {
                    table.addCell (currentGemstoneCell);
                    currentGemstoneCellPrinted = true;
                }
                printLot(lot,table);    // Imprimimos el lote
                oldLot = lot;           // actualizar token
                printJewel(cursor,table,true, true, true,true);
                oldJewelId = jewelId;   // actualizar token
                oldOwner = owner;       // actualizar token
                oldHallmark = hallmark;       // actualizar token
            }else {   // Es el mismo lote
                if(! jewelId.equals (oldJewelId)) {  // Cambio de Joya
                    if (!currentOwnerCellPrinted) {
                        table.addCell (currentOwnerCell);
                        currentOwnerCellPrinted = true;
                    }
                    if (!currentHallmarkCellPrinted) {
                        table.addCell (currentHallmarkCell);
                        currentHallmarkCellPrinted = true;
                    }
                    if (!currentGemstoneCellPrinted) {
                        table.addCell (currentGemstoneCell);
                        currentGemstoneCellPrinted = true;
                    }
                    printJewel (cursor, table, false, true,true,true);
                    oldJewelId = jewelId;// actualizar token
                    oldOwner = owner;   // actualizar token
                    oldHallmark = owner;   // actualizar token
                }else{  // Es la misma joya
                    if (owner != null && ! owner.equals (oldOwner)) {    //Cambio de Propietario
                        printJewel (cursor, table, false, false, true,true);
                        oldOwner = owner;   // actualizar token
                    } else {        // Es el mismo propietario
                        if (hallmark != null && ! hallmark.equals (oldHallmark)) {    //Cambio de Punzón
                            printJewel (cursor, table, false, false, false,true);
                            oldHallmark = hallmark;   // actualizar token
                        } else {        // Es el mismo propietario
                            printJewel (cursor, table, false, false, false,false);
                        }
                    }
                }
            }
        }
        if (!currentOwnerCellPrinted)
            table.addCell (currentOwnerCell);
        if (!currentHallmarkCellPrinted)
            table.addCell (currentHallmarkCell);
        if (!currentGemstoneCellPrinted)
            table.addCell (currentGemstoneCell);

        cursor.close ();
        document.add (table);

        document.close();

        setLocale (oldLocale);
        Toast.makeText(context, "El documento se ha creado correctamente", Toast.LENGTH_SHORT).show();
    }


    private void printJewel(Cursor cursor, PdfPTable table, boolean isFirstJewelOfLot, boolean isDistinctJewel, boolean isDistincOwnerOfJewel, boolean isDistincHallmarkOfJewel){

        String entryJewelType = JewelTypeEntry.NAME;
        String entryDesigner =  DesignerEntry.NAME;
        String entryPeriod = PeriodEntry.NAME;
        String entryObs = JewelEntry.OBS;

        String entryOwner =  OwnerEntry.NAME;
        String entryHallmark =  HallmarkEntry.NAME;
        String entryGemstone =  GemstoneEntry.NAME;
        String entryCut =  CutEntry.NAME;

        String entryAvatar =  JewelEntry.AVATAR_URI;
        int column;
        column = cursor.getColumnIndex(entryJewelType);
        String jewelType = cursor.getString(column);
        column = cursor.getColumnIndex(entryDesigner);
        String designer = cursor.getString(column);
        column = cursor.getColumnIndex(entryPeriod);
        String period = cursor.getString(column);
        column = cursor.getColumnIndex(entryObs);
        String obs = cursor.getString(column);
        column = cursor.getColumnIndex(entryOwner);
        String owner = cursor.getString(column);
        column = cursor.getColumnIndex(entryHallmark);
        String hallmark = cursor.getString(column);
        column = cursor.getColumnIndex(entryGemstone);
        String gemstone = cursor.getString(column);
        column = cursor.getColumnIndex(entryCut);
        String cut = cursor.getString(column);

        column = cursor.getColumnIndex(entryAvatar);
        String avatarUri = cursor.getString(column);
        String uri = JEWEL_FILE_PATH + avatarUri;
        Image image = null;
        if (avatarUri != null){
            // Creating an ImageData object
            try {
                image = Image.getInstance (uri);
                if(image.getHeight () < image.getWidth ()) {
                    image.scaleAbsolute (60F, 40F);
                    image.setRotationDegrees (90);
                }else
                    image.scaleAbsolute (40F,60F);

            } catch (BadElementException | IOException e) {
                e.printStackTrace ();
            }
        }

        PdfPCell c = null;
        // Celdas vacías de lote
        if(!isFirstJewelOfLot && currentOwnerCellPrinted && currentHallmarkCellPrinted && currentGemstoneCellPrinted) {// No es la primera joya del lote. Se deja celda  vacía
            c = new PdfPCell ();
            c.setBorder(Rectangle.NO_BORDER);
            table.addCell (c);  // lote vacío
        }
        if(isDistinctJewel) {   // Nueva joya
            // Imprimimos celdas con valores únicos
            printSingleValues (table, image, jewelType, designer,period,obs);
            gemstonesPrinted.clear ();
            gemstonesList = new List();
            ownersList = new List();
            hallmarksList = new List();

            // Imprimimos primer valor de celda con valores múltiples
            ownersList.add(new ListItem (owner != null ? owner : "",fNormal));
            currentOwnerCell = new PdfPCell ();
            currentOwnerCell.setBorder (1);
            currentOwnerCell.addElement (new Paragraph (owner != null ? context.getString (R.string.owners) + ":\n" : "",fNormal));
            currentOwnerCell.addElement (ownersList);
            currentOwnerCellPrinted = false;

            hallmarksList.add(new ListItem (hallmark != null ? hallmark : "",fNormal));
            currentHallmarkCell = new PdfPCell ();
            currentHallmarkCell.setBorder (1);
            currentHallmarkCell.addElement (new Paragraph (hallmark != null ? context.getString (R.string.hallmarks) + ":\n" : "",fNormal));
            currentHallmarkCell.addElement (hallmarksList);
            currentHallmarkCellPrinted = false;

            gemstonesList.add(new ListItem ((gemstone != null ? gemstone : "") + (cut != null ? " corte " + cut : ""),fNormal));
            currentGemstoneCell = new PdfPCell ();
            currentGemstoneCell.setBorder (1);
            currentGemstoneCell.addElement(new Paragraph (gemstone != null ? context.getString (R.string.gemstones) + ":\n" : "",fNormal));
            currentGemstoneCell.addElement (gemstonesList);
            currentGemstoneCellPrinted = false;
            if(gemstone != null)
                gemstonesPrinted.add(new String[]{gemstone,cut});

        } else {    // Misma joya
            if (isDistincOwnerOfJewel) {    // Nuevo propietario
                // Imprimimos siguiente valor de celda con valores múltiples
                ownersList.add(new ListItem (owner != null ? owner : "",fNormal));
            }else{  // Mismo propietario
                if (isDistincHallmarkOfJewel) {    // Nuevo punzón
                    // Imprimimos siguiente valor de celda con valores múltiples
                    hallmarksList.add(new ListItem (hallmark != null ? hallmark : "",fNormal));
                }else{  // Mismo punzón
                    if(!isInList (gemstonesPrinted,new String[]{gemstone,cut})) {
                        gemstonesPrinted.add(new String[]{gemstone,cut});
                        gemstonesList.add(new ListItem (gemstone + (cut != null ? " " + context.getString (R.string.cut) + " " + cut : ""),fNormal));
                    }
                }
            }
        }
    }

    public static boolean isInList(final ArrayList<String[]> list, final String[] candidate){
        for(final String[] item : list){
            if(Arrays.equals(item, candidate)){
                return true;
            }
        }
        return false;
    }

    private void printSingleValues(PdfPTable table, Image image, String jewelType, String designer, String period, String obs){
        PdfPCell c;
        // Imagen
        c = new PdfPCell ();
        c.setBorder(Rectangle.TOP);
        if(image != null) {
            c.addElement (image);
        }
        else
            c.addElement (new Paragraph (context.getString (R.string.no_image) ,fItalic));
        table.addCell (c);

        c = new PdfPCell ();
        c.setBorder(Rectangle.TOP);
        c.addElement (new Paragraph (context.getString (R.string.type) + ":\n" + (jewelType != null? jewelType:""),fNormal));
        table.addCell (c);

        c = new PdfPCell ();
        c.setBorder(Rectangle.TOP);
        c.addElement (new Paragraph (context.getString (R.string.designed_by) + ":\n" + (designer != null? designer:""),fNormal));
        table.addCell (c);

        c = new PdfPCell ();
        c.setBorder(Rectangle.TOP);
        c.addElement (new Paragraph (context.getString (R.string.period) + ":\n" + (period != null? period:""),fNormal));
        table.addCell (c);

        c = new PdfPCell ();
        c.setBorder(Rectangle.TOP);
        c.addElement (new Paragraph (context.getString (R.string.obs) + ":\n" + (obs != null? obs:""),fNormal));
        table.addCell (c);
    }

    private void printLot(String lot,PdfPTable table){
        PdfPCell c = new PdfPCell ();
        c.setBorder(Rectangle.TOP);
        c.addElement (new Paragraph (context.getString (R.string.lot) + ": "+ (lot != null? " " + lot:""),fNormal));
        table.addCell (c);
    }

    private void printAuction(Cursor cursor,PdfPTable table ){
        String entryAuction =  AuctionEntry.NAME;
        String entryAuctionHouse =  AuctionHouseEntry.NAME;
        String entryCity =  CityEntry.NAME;
        String entryDate =  AuctionEntry.DATE;
        int column;
        column = cursor.getColumnIndex(entryAuction);
        String auction = cursor.getString(column);
        column = cursor.getColumnIndex(entryAuctionHouse);
        String auctionHouse = cursor.getString(column);
        column = cursor.getColumnIndex(entryCity);
        String city = cursor.getString(column);
        column = cursor.getColumnIndex(entryDate);
        String date = cursor.getString(column);

        PdfPCell c = new PdfPCell ();
        c.setColspan (columnsList);
        c.setBackgroundColor (BACKGROUND_VERY_LIGHT_GRAY);
        c.setBorder (0);
        c.addElement (new Paragraph (context.getString (R.string.auction) + ": " + auction,fNormal));
        c.addElement (new Paragraph (context.getString (R.string.auction_house) + ": " + auctionHouse,fNormal));
        c.addElement (new Paragraph (context.getString (R.string.city) + ": " + city,fNormal));
        c.addElement (new Paragraph (context.getString (R.string.date) + ": " + date,fNormal));
        table.addCell (c);
    }

    private void addTitlePage() throws DocumentException{
        title = context.getString (R.string.jewelry_catalog);
        owner = context.getString (R.string.owner_name);
        txtTitle = new Phrase (title,fBold24);
        txtOwner = new Phrase (owner,fBold16);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage (100);
//        table.setHorizontalAlignment (Element.ALIGN_CENTER);
        table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        float h = document.getPageSize ().getHeight ();
        float topMargin = 30F;
        float bottomMargin = 30F;
        float heightTable = h - topMargin - bottomMargin;

        PdfPCell cell = new PdfPCell();
        cell.setBorder (3);
        cell.setFixedHeight (heightTable);
        cell.setVerticalAlignment (Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment (Element.ALIGN_CENTER);
        Paragraph p = new Paragraph (txtTitle);
        Paragraph pp = new Paragraph (txtOwner);
        cell.addElement (p);
        cell.addElement (pp);
        table.addCell(cell);
        try {
            document.add (table);
//            document.add(Chunk.NEXTPAGE);
        } catch (DocumentException e) {
//            document.add(Chunk.NEXTPAGE);
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param context
     */
    public void verifyStoragePermissions(Context context) {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

//    private void addWatermark() throws IOException, DocumentException{
////        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        // pdf
////        PdfContentByte over;
//
//        PdfWriter pdfWriter = PdfWriter
//                .getInstance(document, new FileOutputStream(file.getAbsolutePath ()));
//
//        pdfWriter.setPageEvent(new MyPdfPageEventHelper()); // register the
//        // page event helper
//
////        document.close ();
////        PdfReader reader = new PdfReader (file.getAbsolutePath ());
//////        PdfStamper stamper =  new PdfStamper(reader, outputStream);
////        int pages = reader.getNumberOfPages ();
////        document.open ();
//
////        Paragraph p = new Paragraph(txtWaterMark);
//        // transparency
////        PdfGState gs1 = new PdfGState();
////        gs1.setFillOpacity(0.06f);
//        // Implement transformation matrix usage in order to scale image
////        for (int i = 1; i <= pages; i++) {
////            document.add(new Paragraph(
////                    "This document contains a Watermark text (Background text). "));
////////            PdfPage pdfPage = document.getP (i);
//////            Rectangle pageSize = reader.getPageSizeWithRotation(i);
////////                    .setFontColor(ColorConstants.LIGHT_GRAY,0.5F);
//////            float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
//////            float y = (pageSize.getTop() + pageSize.getBottom()) / 2;
//////            over = stamper.getOverContent(i);
//////            over.saveState();
//////            over.setGState(gs1);
//////            // add text
//////            float angle = (float) Math.PI / 3;
//////            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, txtWaterMark, x, y, angle);
//////            over.restoreState();
////        }
////        try {
////            stamper.close();
////        } catch (DocumentException e) {
////            e.printStackTrace ();
////        } catch (IOException e) {
////            e.printStackTrace ();
////        }
////        reader.close();
//    }

    public File getPDFFile(){
        return file;
    }

    public Uri getUri(){
        return uri;
    }

//    private static class WatermarkedCellRenderer extends CellRenderer {
//        private PdfDocument pdfDocument;
//        private Text content;
//
//        public WatermarkedCellRenderer(PdfDocument pdfDocument, Cell modelElement, Text content) {
//            super(modelElement);
//            this.pdfDocument = pdfDocument;
//            this.content = content;
//        }
//
//        // If a renderer overflows on the next area, iText uses #getNextRenderer() method to create a new renderer for the overflow part.
//        // If #getNextRenderer() isn't overridden, the default method will be used and thus the default rather than the custom
//        // renderer will be created
//        @Override
//        public IRenderer getNextRenderer() {
//            return new WatermarkedCellRenderer(pdfDocument, (Cell) modelElement, content);
//        }
//
//        @Override
//        public void draw(DrawContext drawContext) {
//            super.draw(drawContext);
//
//            Paragraph p = new Paragraph(content).setFontColor(ColorConstants.LIGHT_GRAY);
//            Rectangle rect = getOccupiedAreaBBox();
//            float coordX = (rect.getLeft() + rect.getRight()) / 2;
//            float coordY = (rect.getBottom() + rect.getTop()) / 2;
//            float angle = (float) Math.PI / 3;
//            new Canvas (drawContext.getCanvas(), pdfDocument, rect)
//                .showTextAligned(p, coordX, coordY, getOccupiedArea().getPageNumber(),
//                        TextAlignment.CENTER, VerticalAlignment.MIDDLE, angle)
//                .close();
//        }
//    }
    /**
     * Extend PdfPageEventHelper class and override onEndPagemethod
     * to create page event helper.
     *
     * onEndPage method gets called as soon as document is closed i.e.
     * immediately after document.close(); is called
     */
    class MyPdfPageEventHelper extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter pdfWriter, Document document) {
            PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
            waterMark = context.getString (R.string.property_of) + " " + owner;
            txtWaterMark = new Phrase (waterMark, fBold42WM);

//            Rectangle pageSize = PageSize.A4;
            float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
            float y = (pageSize.getTop() + pageSize.getBottom()) / 2;
            double rotation = Math.toDegrees (Math.atan2 (y,x));
            ColumnText.showTextAligned(pdfContentByte,
                    Element.ALIGN_CENTER, //Keep waterMark center aligned
                    txtWaterMark, x, y,
                    (float) rotation); // 30 aprox is the rotation angle
        }
    }
}
