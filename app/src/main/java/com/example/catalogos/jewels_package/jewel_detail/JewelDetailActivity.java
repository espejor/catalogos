package com.example.catalogos.jewels_package.jewel_detail;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;

public class JewelDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jewel_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID);
//        String fkAuctionId = getIntent().getIntExtra(JewelsByAuctionActivity.EXTRA_);

        JewelDetailFragment fragment = (JewelDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.jewel_detail_container);
        if (fragment == null) {
            fragment = JewelDetailFragment.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.jewel_detail_container, fragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_jewel_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
