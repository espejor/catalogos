package com.example.catalogos.init_package;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.catalogos.R;
import com.example.catalogos.SettingsActivity;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseActivity;
import com.example.catalogos.cities_package.cities.CitiesActivity;
import com.example.catalogos.countries_package.countries.CountriesActivity;
import com.example.catalogos.google_drive_access.ui.GoogleLoginActivity;
import com.example.catalogos.init_package.controller.PagerAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tabCatalogues, tabJewels, tabData;

    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle (R.string.catalogues);
        toolbar.setLogo (R.mipmap.joyas);

        tabLayout = findViewById (R.id.tabLayout);
        viewPager = findViewById (R.id.viewPager);
        tabCatalogues = findViewById (R.id.tabCatalogues);
        tabJewels = findViewById (R.id.tabJewels);
        tabData = findViewById (R.id.tabData);

        pagerAdapter = new PagerAdapter (getSupportFragmentManager (),tabLayout.getTabCount());
        viewPager.setAdapter (pagerAdapter);
        tabLayout.setOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                viewPager.setCurrentItem (tab.getPosition ());
//                switch (tab.getPosition ()){
//                    case 0:
//                }
                pagerAdapter.notifyDataSetChanged ();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab){

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab){

            }
        });

        viewPager.addOnPageChangeListener (new TabLayout.TabLayoutOnPageChangeListener (tabLayout));

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
//            case R.id.manage_home:
//                new GoHome (this).execute ();
//                return true;
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
                showBackupActivity ();
                return true;
            case R.id.settings:
                showSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//
//    private void showManageCities(){
//        Intent intent = new Intent(MainActivity.this, CitiesActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManageCountries(){
//        Intent intent = new Intent(MainActivity.this, CountriesActivity.class);
//        startActivity(intent);
//    }
//
//    private void showManageAuctionsHouse(){
//        Intent intent = new Intent(MainActivity.this, AuctionsHouseActivity.class);
//        startActivity(intent);
//    }

    private void showBackupActivity(){
        Intent intent = new Intent(MainActivity.this, GoogleLoginActivity.class);
        startActivity(intent);
    }

    private void showSettingsActivity(){
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult (requestCode, resultCode, data);
////
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.add_edit_auction_fragment);
//        fragment.onActivityResult(requestCode, resultCode, data);
//    }


}