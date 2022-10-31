package com.example.catalogos.services.pdfviewer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.catalogos.R;
import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PDFViewActivity extends AppCompatActivity {

    public static final String PATH = "PATH";
    private PDFView pdfView;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_pdf_view);

        pdfView = findViewById (R.id.pdf_view);

        Bundle bundle = getIntent ().getExtras ();
        if (bundle!= null){
            file = new File(bundle.getString (PATH,""));
        }

        pdfView.fromFile (file)
                .enableSwipe (true)
                .swipeHorizontal (true)
                .enableDoubletap (true)
                .enableAntialiasing (true)
                .load ();

    }
}
