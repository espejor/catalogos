package com.example.catalogos.owners_package.add_edit_owner;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.owners_package.owners.OwnersActivity;

public class AddEditOwnerActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_OWNER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_owner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String ownerId = getIntent().getStringExtra(OwnersActivity.EXTRA_OWNER_ID);

        setTitle(ownerId == null ? "Añadir Propietario" : "Editar Propietario");

        AddEditOwnerFragment addEditOwnerFragment = (AddEditOwnerFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_owner_container);
        if (addEditOwnerFragment == null) {
            addEditOwnerFragment = AddEditOwnerFragment.newInstance(ownerId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_owner_container, addEditOwnerFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
