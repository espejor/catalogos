package com.example.catalogos.cuts_package.cuts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;


public class CutsActivity extends AppCompatActivity {

    public static final String EXTRA_CUT_ID = "extra_cut_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager= getSupportFragmentManager();
        CutsFragment fragment = (CutsFragment) fragmentManager.findFragmentById (R.id.fragment_cuts);


        if (fragment == null) {
            fragment = CutsFragment.newInstance();

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.cuts_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
