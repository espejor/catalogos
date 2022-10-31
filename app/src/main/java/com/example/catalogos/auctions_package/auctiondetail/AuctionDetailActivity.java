package com.example.catalogos.auctions_package.auctiondetail;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.auctions_package.auctions.AuctionsActivity;

public class AuctionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra(AuctionsActivity.EXTRA_AUCTION_ID);

        AuctionDetailFragment fragment = (AuctionDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.auction_detail_container);
        if (fragment == null) {
            fragment = AuctionDetailFragment.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.auction_detail_container, fragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_auction_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
