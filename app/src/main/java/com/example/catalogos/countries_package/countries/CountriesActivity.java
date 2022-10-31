package com.example.catalogos.countries_package.countries;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;


public class CountriesActivity extends AppCompatActivity {

    public static final String EXTRA_COUNTRY_ID = "extra_country_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countries);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager= getSupportFragmentManager();
        CountriesFragment fragment = (CountriesFragment) fragmentManager.findFragmentById (R.id.fragment_countries);


        if (fragment == null) {
            fragment = CountriesFragment.newInstance();

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.countries_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
