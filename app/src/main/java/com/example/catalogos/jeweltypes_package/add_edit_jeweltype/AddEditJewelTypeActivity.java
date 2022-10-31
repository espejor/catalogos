package com.example.catalogos.jeweltypes_package.add_edit_jeweltype;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesActivity;

public class AddEditJewelTypeActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_JEWEL_TYPE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_jeweltype);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String jewelTypeId = getIntent().getStringExtra(JewelTypesActivity.EXTRA_JEWEL_TYPE_ID);

        setTitle(jewelTypeId == null ? "Añadir Tipo de Joya" : "Editar Tipo de Joya");

        AddEditJewelTypeFragment addEditJewelTypeFragment = (AddEditJewelTypeFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_jeweltype_container);
        if (addEditJewelTypeFragment == null) {
            addEditJewelTypeFragment = AddEditJewelTypeFragment.newInstance(jewelTypeId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_jeweltype_container, addEditJewelTypeFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
