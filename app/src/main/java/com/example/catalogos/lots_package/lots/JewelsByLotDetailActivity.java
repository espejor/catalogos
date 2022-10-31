package com.example.catalogos.lots_package.lots;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;
import com.example.catalogos.auctions_package.auctions.AuctionsActivity;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;
import com.example.catalogos.services.GoHome;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;


public class JewelsByLotDetailActivity extends AppCompatActivity {
    public static final String EXTRA_JEWEL_ID = "extra_jewel_id";

    JewelsByLotDetailFragment fragment;

//    public static final String EXTRA_AUCTION_ID = "extra_auction_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lots);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String jewelId = getIntent().getStringExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID);
        String lotNumber = getIntent().getStringExtra(JewelsByAuctionActivity.EXTRA_LOT_NUMBER);
        int fkAuctionId = getIntent().getIntExtra(EXTRA_FK_AUCTION_ID,0);

        String auctionId = getIntent().getStringExtra(AuctionsActivity.EXTRA_AUCTION_ID);

        FragmentManager fragmentManager= getSupportFragmentManager();
        fragment = (JewelsByLotDetailFragment) fragmentManager.findFragmentById (R.id.fragment_lots);


        if (fragment == null) {
            fragment = JewelsByLotDetailFragment.newInstance(fkAuctionId,jewelId,lotNumber);

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.lots_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jewel_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_home:
                new GoHome (this).execute ();
                return true;
            case R.id.action_edit:
                fragment.showEditScreen();
                break;
            case R.id.action_delete:
                fragment.deleteJewel ();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.manage_jeweltypes:
//                showManageLotTypes();
//                return true;
//            case R.id.manage_designers:
//                showManageDesigners();
//                return true;
//            case R.id.manage_cuts:
//                showManageCuts();
//                return true;
//            case R.id.manage_owners:
//                showManageOwners();
//                return true;
//            case R.id.manage_gemstones:
//                showManageGemstones();
//                return true;
//            case R.id.manage_periods:
//                showManagePeriods();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void showManageLotTypes(){
//        Intent intent = new Intent(JewelsByLotDetailActivity.this, JewelTypesActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManageDesigners(){
//        Intent intent = new Intent(JewelsByLotDetailActivity.this, DesignersActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManagePeriods(){
//        Intent intent = new Intent(JewelsByLotDetailActivity.this, PeriodsActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManageGemstones(){
//        Intent intent = new Intent(JewelsByLotDetailActivity.this, GemstonesActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManageOwners(){
//        Intent intent = new Intent(JewelsByLotDetailActivity.this, OwnersActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManageCuts(){
//        Intent intent = new Intent(JewelsByLotDetailActivity.this, CutsActivity.class);
//        startActivity(intent);
//    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
