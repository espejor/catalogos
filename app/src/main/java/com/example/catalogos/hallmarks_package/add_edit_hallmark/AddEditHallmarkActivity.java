package com.example.catalogos.hallmarks_package.add_edit_hallmark;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarksActivity;

public class AddEditHallmarkActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_HALLMARK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_hallmark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String hallmarkId = getIntent().getStringExtra(HallmarksActivity.EXTRA_HALLMARK_ID);

        setTitle(hallmarkId == null ? "Añadir Propietario" : "Editar Propietario");

        AddEditHallmarkFragment addEditHallmarkFragment = (AddEditHallmarkFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_hallmark_container);
        if (addEditHallmarkFragment == null) {
            addEditHallmarkFragment = AddEditHallmarkFragment.newInstance(hallmarkId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_hallmark_container, addEditHallmarkFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
