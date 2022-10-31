package com.example.catalogos.countries_package.add_edit_country;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.countries_package.countries.CountriesActivity;

public class AddEditCountryActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_COUNTRY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_country);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String countryId = getIntent().getStringExtra(CountriesActivity.EXTRA_COUNTRY_ID);

        setTitle(countryId == null ? "Añadir País" : "Editar País");

        AddEditCountryFragment addEditCountryFragment = (AddEditCountryFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_country_container);
        if (addEditCountryFragment == null) {
            addEditCountryFragment = AddEditCountryFragment.newInstance(countryId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_country_container, addEditCountryFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
