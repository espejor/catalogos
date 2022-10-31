package com.example.catalogos.jewels_package.jewels;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;
import com.example.catalogos.cuts_package.cuts.CutsActivity;
import com.example.catalogos.designers_package.designers.DesignersActivity;
import com.example.catalogos.gemstones_package.gemstones.GemstonesActivity;
import com.example.catalogos.init_package.MainActivity;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesActivity;
import com.example.catalogos.owners_package.owners.OwnersActivity;
import com.example.catalogos.pdf_creator.PDFCreator;
import com.example.catalogos.periods_package.periods.PeriodsActivity;
import com.example.catalogos.services.GoHome;
import com.example.catalogos.services.pdfviewer.SelectViewerForPDFDialog;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_BUNDLE_SEARCH;
import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;

import java.io.File;


public class JewelsByAuctionActivity extends AppCompatActivity
        implements SelectViewerForPDFDialog.OnSelectViewerForPDFDialogListener {

    public static final String EXTRA_JEWEL_ID = "extra_jewel_id";
    public static final String EXTRA_LOT_NUMBER = "extra_lot_number";
//    public static final String EXTRA_AUCTION_ID = "extra_auction_id";
    private int mFkAuctionId;
    private Bundle mSearchdata;
    JewelsByAuctionFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jewels);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle parameters = this.getIntent ().getExtras ();
        if (parameters != null) {
            mFkAuctionId = parameters.getInt (EXTRA_FK_AUCTION_ID);
            mSearchdata = parameters.getBundle (EXTRA_BUNDLE_SEARCH);
        }

        FragmentManager fragmentManager= getSupportFragmentManager();
        fragment = (JewelsByAuctionFragment) fragmentManager.findFragmentById (R.id.fragment_jewels);

        if (fragment == null) {
            fragment = JewelsByAuctionFragment.newInstance();
            fragment.setArguments (parameters);

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.jewels_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jewels_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.create_pdf:
                createPDF ();
                return true;
            case R.id.manage_home:
                new GoHome (this).execute ();
                return true;
            case R.id.manage_jeweltypes:
                showManageJewelTypes();
                return true;
            case R.id.manage_designers:
                showManageDesigners();
                return true;
            case R.id.manage_cuts:
                showManageCuts();
                return true;
            case R.id.manage_owners:
                showManageOwners();
                return true;
            case R.id.manage_gemstones:
                showManageGemstones();
                return true;
            case R.id.manage_periods:
                showManagePeriods();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createPDF(){
        fragment.createPDF();
    }

    private void showManageJewelTypes(){
        Intent intent = new Intent(JewelsByAuctionActivity.this, JewelTypesActivity.class);
        startActivity(intent);
    }

    private void showManageDesigners(){
        Intent intent = new Intent(JewelsByAuctionActivity.this, DesignersActivity.class);
        startActivity(intent);
    }

    private void showManagePeriods(){
        Intent intent = new Intent(JewelsByAuctionActivity.this, PeriodsActivity.class);
        startActivity(intent);
    }

    private void showManageGemstones(){
        Intent intent = new Intent(JewelsByAuctionActivity.this, GemstonesActivity.class);
        startActivity(intent);
    }

    private void showManageOwners(){
        Intent intent = new Intent(JewelsByAuctionActivity.this, OwnersActivity.class);
        startActivity(intent);
    }

    private void showManageCuts(){
        Intent intent = new Intent(JewelsByAuctionActivity.this, CutsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onLocal(String filePath, File pdfFile){
        fragment.onLocal (filePath,pdfFile);
    }

    @Override
    public void onExternal(String filePath, File pdfFile){
        fragment.onExternal (filePath,pdfFile);
    }
}
