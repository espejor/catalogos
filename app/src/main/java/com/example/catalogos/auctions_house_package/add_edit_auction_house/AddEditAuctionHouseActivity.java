package com.example.catalogos.auctions_house_package.add_edit_auction_house;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseActivity;

public class AddEditAuctionHouseActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_AUCTION_HOUSE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_auction_house);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String auctionHouseId = getIntent().getStringExtra(AuctionsHouseActivity.EXTRA_AUCTION_HOUSE_ID);

        setTitle(auctionHouseId == null ? "Añadir Casa de Subasta" : "Editar Casa de Subasta");

        AddEditAuctionHouseFragment addEditAuctionHouseFragment = (AddEditAuctionHouseFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_auction_house_container);
        if (addEditAuctionHouseFragment == null) {
            addEditAuctionHouseFragment = AddEditAuctionHouseFragment.newInstance(auctionHouseId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_auction_house_container, addEditAuctionHouseFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
