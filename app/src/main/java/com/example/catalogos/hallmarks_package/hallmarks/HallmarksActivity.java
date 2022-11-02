package com.example.catalogos.hallmarks_package.hallmarks;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.catalogos.R;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarksFragment;


public class HallmarksActivity extends AppCompatActivity {

    public static final String EXTRA_HALLMARK_ID = "extra_hallmark_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hallmarks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Poner el botón de marcha atrás
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager= getSupportFragmentManager();
        HallmarksFragment fragment = (HallmarksFragment) fragmentManager.findFragmentById (R.id.fragment_hallmarks);


        if (fragment == null) {
            fragment = HallmarksFragment.newInstance();

            FragmentManager fm =getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.hallmarks_container, fragment);
            ft.commit();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}