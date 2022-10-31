package com.example.catalogos.pdf_creator;


import static com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.*;
import static com.example.catalogos.cities_package.cities_data.CitiesContract.*;
import static com.example.catalogos.designers_package.designers_data.DesignersContract.*;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;
import static com.example.catalogos.jewels_package.jewels_data.JewelsContract.*;
import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA;
import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD;
import static com.itextpdf.io.font.constants.StandardFonts.HELVETICA_OBLIQUE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PDFCreator {
    private final Context context;
    private final String fileName;
    File file;
    private Cursor cursor = null;
    int columnsList = 6;
    private Document document;
    private PdfWriter writer;
    private PdfDocument pdfDocument;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String title = "Catálogo de Joyas";
    private String owner = "Alejandro Espejo Fernández";
    private String waterMark = "Propiedad de \n" + owner;
    private Text txtTitle;
    private Text txtOwner;
    private Text txtBody;
    private Text txtWaterMark;

    private Cell currentOwnerCell;
    private Cell currentGemstoneCell;
    private boolean currentOwnerCellPrinted = true;
    private boolean currentGemstoneCellPrinted = true;

    com.itextpdf.layout.element.List ownersList = new com.itextpdf.layout.element.List();
    com.itextpdf.layout.element.List gemstonesList = new com.itextpdf.layout.element.List();

    private PdfFont fBold;
    private PdfFont fNormal;
    private PdfFont fItalic;
    private Uri uri;
    private String oldOwner;
    private ArrayList<String[]> gemstonesPrinted = new ArrayList<> ();

    public PDFCreator(Context context,String filename){
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
        try {
            fBold = PdfFontFactory.createFont(HELVETICA_BOLD);
            fNormal = PdfFontFactory.createFont(HELVETICA);
            fItalic = PdfFontFactory.createFont(HELVETICA_OBLIQUE);
        } catch (IOException e) {
            e.printStackTrace ();
        }

        txtTitle = new Text (title).setFont (fBold)
                .setFontSize (24);
        txtOwner = new Text (owner).setFont (fBold)
                .setFontSize (16);
        txtBody = new Text ("").setFont (fNormal)
                .setFontSize (10);
        txtWaterMark = new Text (waterMark).setFont (fBold)
                .setFontSize (42);
    }

    public PDFCreator(Context context){
//        Date date = new Date();
//        String year = String.valueOf (date.getYear ());
//        String month = String.valueOf (date.getMonth ());
//        String day = String.valueOf (date.getDay ());
//        String hour = String.valueOf (date.getHours ());
//        String min = String.valueOf (date.getMinutes ());
//        String sec = String.valueOf (date.getSeconds ());
//        String dateStr = year + month+day+hour+min+sec;

        this (context,"catalogoTemp");
    }

    private void createPDF(){
        String pdfPath = context.getFilesDir () + "/tempPDF";
//            pdfPath = "/storage/sdcard0/"
//                    + Environment.DIRECTORY_DOWNLOADS;

        File pdfDir = new File(pdfPath);
        if (! pdfDir.exists ())
            pdfDir.mkdirs ();
        file = new File(pdfDir,fileName);
        uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        try {
            writer = new PdfWriter (String.valueOf(file));
            pdfDocument = new PdfDocument(writer);
            document = new Document (pdfDocument);
        } catch (FileNotFoundException e) {
            e.printStackTrace ();
        }
    }

    public void createListOfImagesPDF(Cursor cursor){
        this.cursor = cursor;
        int columns = 4;

        // agregar el título
        addTitlePage ();

        // Adding paragraphs to document
        Table table = new Table (columns).useAllAvailableWidth ();

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
                ImageData data = null;
                try {
                    data = ImageDataFactory.create(uri);
                } catch (MalformedURLException e) {
                    e.printStackTrace ();
                }

                UnitValue tableWidth = table.getWidth ();
                // Creating an Image object
                image = new Image(data);
                image.setHeight (new UnitValue (UnitValue.PERCENT,tableWidth.getValue ()/columns));
                image.setWidth (tableWidth.getValue ()/columns);
            }

            Cell c = new Cell ();
            c.setHeight (c.getWidth ())
                    .setPadding (4F);
            if (image != null) {
                c.add (image.setAutoScale (true));
            }
            table.addCell (c);

        }
        document.add (table);

        // agregar marca de agua
        addWatermark ();

        document.close();

        Toast.makeText(context, "El documento se ha creado correctamente", Toast.LENGTH_SHORT).show();

    }


    public void createListPDF(Cursor cursor){
        this.cursor = cursor;

        // agregar el título
        addTitlePage ();

        Table table = new Table(columnsList).useAllAvailableWidth();

        // Lista
        //Orden de los cortes
        //fkAuctionId, lot,jewelFullDataView.id , owner,gemstone,cut
        // Control de cortes de bucle
        String oldAuction = "";
        String oldLot = "";
        String oldJewelId = "";
//        String oldOwner = "";
        String oldGemstone = "";
        String oldCut = "";

        // Recorrer el cursor para imprimir todas las joyas
        for (int i = 0; i < cursor.getCount (); i++) {
            cursor.moveToPosition (i);

            // Get valores.
            String entryAuction =  AuctionEntry.NAME;
            String entryLot =  JewelEntry.LOT;
            String entryJewelId = JewelEntry.ID;
            String entryOwner =  OwnerEntry.NAME;
            String entryGemstone =  GemstoneEntry.NAME;
            String entryCut =  CutEntry.NAME;


            int column;
            column = cursor.getColumnIndex(entryAuction);
            String auction = cursor.getString(column);
            column = cursor.getColumnIndex(entryLot);
            String lot = cursor.getString(column);
            column = cursor.getColumnIndex(entryJewelId);
            String jewelId = cursor.getString(column);
            column = cursor.getColumnIndex(entryOwner);
            String owner = cursor.getString(column);
//            column = cursor.getColumnIndex(entryGemstone);
//            String gemstone = cursor.getString(column);
//            column = cursor.getColumnIndex(entryCut);
//            String cut = cursor.getString(column);

            if(! auction.equals (oldAuction)){// Cambio de Subasta
                if (!currentOwnerCellPrinted) {
                    table.addCell (currentOwnerCell);
                    currentOwnerCellPrinted = true;
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
            }
            if(! lot.equals (oldLot)){  // Cambio de lote
                if (!currentOwnerCellPrinted) {
                    table.addCell (currentOwnerCell);
                    currentOwnerCellPrinted = true;
                }
                if (!currentGemstoneCellPrinted) {
                    table.addCell (currentGemstoneCell);
                    currentGemstoneCellPrinted = true;
                }
                printLot(lot,table);    // Imprimimos el lote
                oldLot = lot;           // actualizar token
                printJewel(cursor,table,true, true, true);
                oldJewelId = jewelId;   // actualizar token
                oldOwner = owner;       // actualizar token
            }else {   // Es el mismo lote
                if(! jewelId.equals (oldJewelId)) {  // Cambio de Joya
                    if (!currentOwnerCellPrinted) {
                        table.addCell (currentOwnerCell);
                        currentOwnerCellPrinted = true;
                    }
                    if (!currentGemstoneCellPrinted) {
                        table.addCell (currentGemstoneCell);
                        currentGemstoneCellPrinted = true;
                    }
                    printJewel (cursor, table, false, true,true);
                    oldJewelId = jewelId;// actualizar token
                    oldOwner = owner;   // actualizar token
                }else{  // Es la misma joya
                    if (! owner.equals (oldOwner)) {    //Cambio de Propietario
                        printJewel (cursor, table, false, false, true);
                        oldOwner = owner;   // actualizar token
                    } else {        // Es el mismo propietario
                        printJewel (cursor, table, false, false, false);
                    }
                }
            }
        }
        if (!currentOwnerCellPrinted)
            table.addCell (currentOwnerCell);
        if (!currentGemstoneCellPrinted)
            table.addCell (currentGemstoneCell);

        cursor.close ();
        document.add (table);

        // agregar marca de agua
        addWatermark ();

        document.close();

        Toast.makeText(context, "El documento se ha creado correctamente", Toast.LENGTH_SHORT).show();
    }


    private void printJewel(Cursor cursor, Table table, boolean isFirstJewelOfLot, boolean isDistinctJewel, boolean isDistincOwnerOfJewel){

        String entryJewelType = JewelTypeEntry.NAME;
        String entryDesigner =  DesignerEntry.NAME;
        String entryOwner =  OwnerEntry.NAME;
        String entryGemstone =  GemstoneEntry.NAME;
        String entryCut =  CutEntry.NAME;

        String entryAvatar =  JewelEntry.AVATAR_URI;
        int column;
        column = cursor.getColumnIndex(entryJewelType);
        String jewelType = cursor.getString(column);
        column = cursor.getColumnIndex(entryDesigner);
        String designer = cursor.getString(column);
        column = cursor.getColumnIndex(entryOwner);
        String owner = cursor.getString(column);
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
            ImageData data = null;
            try {
                data = ImageDataFactory.create(uri);
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            }

            // Creating an Image object
            image = new Image(data);
            image.setHeight (40F);
            image.setWidth (40F);
        }

        Cell c = null;
        // Celdas vacías de lote
        if(!isFirstJewelOfLot && currentOwnerCellPrinted && currentGemstoneCellPrinted) {// No es la primera joya del lote. Se deja celda  vacía
            c = new Cell ()
                    .setBorder(Border.NO_BORDER)
                    .setBorderBottom (new SolidBorder (1F));
            table.addCell (c);  // lote vacío
        }
        // Imprimimos celdas con valores únicos
        if(isDistinctJewel) {   // Nueva joya
            printSingleValues (table, image, jewelType, designer);
            gemstonesPrinted.clear ();
            gemstonesList = new com.itextpdf.layout.element.List();
            ownersList = new com.itextpdf.layout.element.List();

            // Imprimimos primer valor de celda con valores múltiples
            ownersList.add(owner != null ? owner : "");
            currentOwnerCell = new Cell ()
                    .setBorder (Border.NO_BORDER)
                    .setBorderBottom (new SolidBorder (1F))
                    .add (new Paragraph (owner != null ? "Propietarios:\n" : ""))
                    .add (ownersList);
            currentOwnerCellPrinted = false;

            gemstonesList.add((gemstone != null ? gemstone : "") + (cut != null ? " corte " + cut : ""));
            currentGemstoneCell = new Cell ()
                    .setBorder (Border.NO_BORDER)
                    .setBorderBottom (new SolidBorder (1F))
                    .add(new Paragraph (gemstone != null ? "Gemas:\n" : ""))
                    .add (gemstonesList);
            currentGemstoneCellPrinted = false;
            if(gemstone != null)
                gemstonesPrinted.add(new String[]{gemstone,cut});

        } else {    // Misma joya
            if (isDistincOwnerOfJewel) {    // Nuevo propietario
                // Imprimimos siguiente valor de celda con valores múltiples
                ownersList.add(owner != null ? owner : "");
            }else{  // Mismo propietario
                if(!isInList (gemstonesPrinted,new String[]{gemstone,cut})) {
                    gemstonesPrinted.add(new String[]{gemstone,cut});
                    gemstonesList.add(gemstone + (cut != null ? " corte " + cut : ""));
                }
            }
        }
     }
    public static boolean isInList(final List<String[]> list, final String[] candidate){
        for(final String[] item : list){
            if(Arrays.equals(item, candidate)){
                return true;
            }
        }
        return false;
    }

    private void printSingleValues(Table table, Image image, String jewelType, String designer){
        Cell c;
        // Imagen
        c = new Cell ()
                .setBorder(Border.NO_BORDER)
                .setBorderBottom (new SolidBorder (1F));
        if(image != null)
            c.add (image);
        else
            c.add(new Paragraph ("Sin imagen"));
        table.addCell (c);

        c = new Cell ()
                .setBorder(Border.NO_BORDER)
                .setBorderBottom (new SolidBorder (1F))
                .add(new Paragraph ("Tipo: " + (jewelType != null? jewelType:"")));
        table.addCell (c);

        c = new Cell ()
                .setBorder(Border.NO_BORDER)
                .setBorderBottom (new SolidBorder (1F))
                .add(new Paragraph ("Diseñador: " + (designer != null? designer:"")));
        table.addCell (c);
    }

    private void printLot(String lot,Table table){
        Cell c = new Cell ()
                    .setBorder (Border.NO_BORDER)
                    .add(new Paragraph ("Lote: " + (lot != null? lot:"")));
        table.addCell (c);
    }

    private void printAuction(Cursor cursor,Table table ){
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

        Cell c = new Cell (1,columnsList)
                .setBackgroundColor (ColorConstants.LIGHT_GRAY)
                .setBorder (Border.NO_BORDER)
                .add(new Paragraph ("Subasta: " + auction))
                .add(new Paragraph ("Casa de Subastas: " + auctionHouse))
                .add(new Paragraph ("Ciudad: " + city))
                .add(new Paragraph ("Fecha: " + date));
        table.addCell (c);
    }

    private void addTitlePage(){
        Table table = new Table(1).useAllAvailableWidth();

        document.getPdfDocument ().getDefaultPageSize ().applyMargins (0,0,0,0,false);
        float h = document.getPdfDocument ().getDefaultPageSize ().getHeight ();
        table.setHeight (700 );
        table.setBorder (Border.NO_BORDER);

        Cell cell = new Cell();
        cell.setBorder (Border.NO_BORDER);
        cell.setHeight (new UnitValue (UnitValue.PERCENT,100));
        cell.setVerticalAlignment (VerticalAlignment.MIDDLE);
        cell.setHorizontalAlignment (HorizontalAlignment.CENTER);
        Paragraph p = new Paragraph (txtTitle);
        p.setHorizontalAlignment (HorizontalAlignment.CENTER);
        Paragraph pp = new Paragraph (txtOwner);
        pp.setHorizontalAlignment (HorizontalAlignment.CENTER);
        cell.add (p);
        cell.add (pp);        table.addCell(cell);

        document.add (table);
        document.add (new AreaBreak ());
    }


    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param context
     */
    public static void verifyStoragePermissions(Context context) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void addWatermark(){
        // Implement transformation matrix usage in order to scale image
        for (int i = 1; i <= pdfDocument.getNumberOfPages(); i++) {

            PdfPage pdfPage = pdfDocument.getPage(i);
            Rectangle pageSize = pdfPage.getPageSizeWithRotation();

            Paragraph p = new Paragraph(txtWaterMark).setFontColor(ColorConstants.LIGHT_GRAY,0.5F);
            float x = (pageSize.getLeft() + pageSize.getRight()) / 2;
            float y = (pageSize.getTop() + pageSize.getBottom()) / 2;
//            PdfCanvas over = new PdfCanvas(pdfDocument.getPage(i));
            float angle = (float) Math.PI / 3;
            document.showTextAligned(p, x, y, i, TextAlignment.CENTER, VerticalAlignment.TOP, angle);
        }

    }

    public File getPDFFile(){
        return file;
    }

    public Uri getUri(){
        return uri;
    }

    private static class WatermarkedCellRenderer extends CellRenderer {
        private PdfDocument pdfDocument;
        private Text content;

        public WatermarkedCellRenderer(PdfDocument pdfDocument, Cell modelElement, Text content) {
            super(modelElement);
            this.pdfDocument = pdfDocument;
            this.content = content;
        }

        // If a renderer overflows on the next area, iText uses #getNextRenderer() method to create a new renderer for the overflow part.
        // If #getNextRenderer() isn't overridden, the default method will be used and thus the default rather than the custom
        // renderer will be created
        @Override
        public IRenderer getNextRenderer() {
            return new WatermarkedCellRenderer(pdfDocument, (Cell) modelElement, content);
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);

            Paragraph p = new Paragraph(content).setFontColor(ColorConstants.LIGHT_GRAY);
            Rectangle rect = getOccupiedAreaBBox();
            float coordX = (rect.getLeft() + rect.getRight()) / 2;
            float coordY = (rect.getBottom() + rect.getTop()) / 2;
            float angle = (float) Math.PI / 3;
            new Canvas (drawContext.getCanvas(), pdfDocument, rect)
                .showTextAligned(p, coordX, coordY, getOccupiedArea().getPageNumber(),
                        TextAlignment.CENTER, VerticalAlignment.MIDDLE, angle)
                .close();
        }
    }
}
