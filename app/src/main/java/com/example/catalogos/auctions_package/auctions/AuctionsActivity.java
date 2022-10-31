package com.example.catalogos.auctions_package.auctions;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseActivity;
import com.example.catalogos.cities_package.cities.CitiesActivity;
import com.example.catalogos.countries_package.countries.CountriesActivity;
import com.example.catalogos.google_drive_access.ui.GoogleLoginActivity;
import com.example.catalogos.services.GoHome;


public class AuctionsActivity extends AppCompatActivity {

    public static final String EXTRA_AUCTION_ID = "extra_auction_id";
    public static final String EXTRA_FK_AUCTION_ID = "extra_fk_auction_id";
    public static final String EXTRA_BUNDLE_SEARCH = "extra_search_bundle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager= getSupportFragmentManager();
        AuctionsFragment fragment = (AuctionsFragment) fragmentManager.findFragmentById (R.id.fragment_auctions);


        if (fragment == null) {
            fragment = AuctionsFragment.newInstance();

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.auctions_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auctions_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.manage_home:
                new GoHome (this).execute ();
                return true;
//            case R.id.manage_auctions_house:
//                showManageAuctionsHouse();
//                return true;
//            case R.id.manage_cities:
//                showManageCities();
//                return true;
//            case R.id.manage_countries:
//                showManageCountries();
//                return true;
            case R.id.backup:
                showLoginActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showManageCities(){
        Intent intent = new Intent(AuctionsActivity.this, CitiesActivity.class);
        startActivity(intent);
    }

    private void showManageCountries(){
        Intent intent = new Intent(AuctionsActivity.this, CountriesActivity.class);
        startActivity(intent);
    }

    private void showManageAuctionsHouse(){
        Intent intent = new Intent(AuctionsActivity.this, AuctionsHouseActivity.class);
        startActivity(intent);
    }

    private void showLoginActivity(){
        Intent intent = new Intent(AuctionsActivity.this, GoogleLoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
