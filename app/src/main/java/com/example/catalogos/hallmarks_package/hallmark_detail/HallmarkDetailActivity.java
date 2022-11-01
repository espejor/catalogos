package com.example.catalogos.hallmarks_package.hallmark_detail;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarksActivity;

public class HallmarkDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hallmark_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra(HallmarksActivity.EXTRA_HALLMARK_ID);

        HallmarkDetailFragment fragment = (HallmarkDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.hallmark_detail_container);
        if (fragment == null) {
            fragment = HallmarkDetailFragment.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.hallmark_detail_container, fragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hallmark_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
