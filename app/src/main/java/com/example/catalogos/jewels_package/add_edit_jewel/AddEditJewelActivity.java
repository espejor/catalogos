package com.example.catalogos.jewels_package.add_edit_jewel;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;
import static com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity.EXTRA_LOT_NUMBER;

public class AddEditJewelActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_JEWEL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_jewel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String jewelId = getIntent().getStringExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID);
        String lotNumber = getIntent().getStringExtra(EXTRA_LOT_NUMBER);
        int fkAuctionId = getIntent().getIntExtra(EXTRA_FK_AUCTION_ID,0);

        setTitle(jewelId == null ? "Añadir Joya" : "Editar Joya");

        AddEditJewelFragment addEditJewelFragment = (AddEditJewelFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_jewel_container);
        if (addEditJewelFragment == null) {
            addEditJewelFragment = AddEditJewelFragment.newInstance(jewelId,fkAuctionId,lotNumber);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_jewel_container, addEditJewelFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult (requestCode, resultCode, data);
//
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.add_edit_jewel_container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
