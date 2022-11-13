package com.example.catalogos.auctions_package.addeditauction;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.auctions_package.auctions.AuctionsActivity;

public class AddEditAuctionActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_AUCTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_auction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String auctionId = getIntent().getStringExtra(AuctionsActivity.EXTRA_AUCTION_ID);

        setTitle(auctionId == null ? "Añadir Subasta" : "Editar Subasta");

        AddEditAuctionFragment addEditAuctionFragment = (AddEditAuctionFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_auction_container);
        if (addEditAuctionFragment == null) {
            addEditAuctionFragment = AddEditAuctionFragment.newInstance(auctionId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_auction_container, addEditAuctionFragment)
                    .commit();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
