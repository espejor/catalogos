package com.example.catalogos.cuts_package.add_edit_cut;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.cuts_package.cuts.CutsActivity;

public class AddEditCutActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_CUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_cut);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String cutId = getIntent().getStringExtra(CutsActivity.EXTRA_CUT_ID);

        setTitle(cutId == null ? "Añadir Tipo de Talla" : "Editar Tipo de Talla");

        AddEditCutFragment addEditCutFragment = (AddEditCutFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_cut_container);
        if (addEditCutFragment == null) {
            addEditCutFragment = AddEditCutFragment.newInstance(cutId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_cut_container, addEditCutFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
