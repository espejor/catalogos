package com.example.catalogos.cities_package.cities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;


public class CitiesActivity extends AppCompatActivity {

    public static final String EXTRA_CITY_ID = "extra_city_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager= getSupportFragmentManager();
        CitiesFragment fragment = (CitiesFragment) fragmentManager.findFragmentById (R.id.fragment_cities);


        if (fragment == null) {
            fragment = CitiesFragment.newInstance();

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.cities_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
