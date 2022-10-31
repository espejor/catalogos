package com.example.catalogos.gemstones_package.add_edit_gemstone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.gemstones_package.gemstones.GemstonesActivity;

public class AddEditGemstoneActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_GEMSTONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_gemstone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String gemstoneId = getIntent().getStringExtra(GemstonesActivity.EXTRA_GEMSTONE_ID);

        setTitle(gemstoneId == null ? "Añadir Piedra Preciosa" : "Editar Piedra Preciosa");

        AddEditGemstoneFragment addEditGemstoneFragment = (AddEditGemstoneFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_gemstone_container);
        if (addEditGemstoneFragment == null) {
            addEditGemstoneFragment = AddEditGemstoneFragment.newInstance(gemstoneId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_gemstone_container, addEditGemstoneFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
