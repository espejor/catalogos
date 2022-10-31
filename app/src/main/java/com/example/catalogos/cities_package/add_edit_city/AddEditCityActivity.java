package com.example.catalogos.cities_package.add_edit_city;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.cities_package.cities.CitiesActivity;

public class AddEditCityActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_CITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String cityId = getIntent().getStringExtra(CitiesActivity.EXTRA_CITY_ID);

        setTitle(cityId == null ? "Añadir Ciudad" : "Editar Ciudad");

        AddEditCityFragment addEditCityFragment = (AddEditCityFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_city_container);
        if (addEditCityFragment == null) {
            addEditCityFragment = AddEditCityFragment.newInstance(cityId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_city_container, addEditCityFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
