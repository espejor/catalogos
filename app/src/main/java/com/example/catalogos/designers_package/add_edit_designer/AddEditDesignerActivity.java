package com.example.catalogos.designers_package.add_edit_designer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.designers_package.designers.DesignersActivity;

public class AddEditDesignerActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_DESIGNER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_designer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String designerId = getIntent().getStringExtra(DesignersActivity.EXTRA_DESIGNER_ID);

        setTitle(designerId == null ? "Añadir Diseñador" : "Editar Diseñador");

        AddEditDesignerFragment addEditDesignerFragment = (AddEditDesignerFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_designer_container);
        if (addEditDesignerFragment == null) {
            addEditDesignerFragment = AddEditDesignerFragment.newInstance(designerId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_designer_container, addEditDesignerFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
