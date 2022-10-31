package com.example.catalogos.auctions_house_package.auctions_house;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.add_edit_auction_house.AddEditAuctionHouseActivity;
import com.example.catalogos.auctions_house_package.auction_house_detail.AuctionHouseDetailActivity;
import com.example.catalogos.database.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class AuctionsHouseFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_AUCTION_HOUSE = 2;

    private DbHelper dbHelper;

    private ListView mAuctionsHouseList;
    private AuctionsHouseCursorAdapter mAuctionsHouseAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public AuctionsHouseFragment() {
        // Required empty public constructor
    }

    public static AuctionsHouseFragment newInstance() {
        return new AuctionsHouseFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auctions_house, container, false);

        // Referencias UI
        mAuctionsHouseList = root.findViewById(R.id.auctions_house_list);
        mAuctionsHouseAdapter = new AuctionsHouseCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mAuctionsHouseList.setAdapter(mAuctionsHouseAdapter);

        // Eventos
        mAuctionsHouseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mAuctionsHouseAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(AuctionHouseEntry.ID));

                showDetailScreen(currentAuctionId);
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScreen();
            }
        });


//        getActivity().deleteDatabase(DbHelper.DATABASE_NAME);

        // Instancia de helper
        dbHelper = new DbHelper(getActivity());

        // Carga de datos
        loadAuctionsHouse();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditAuctionHouseActivity.REQUEST_ADD_AUCTION_HOUSE:
                    showSuccessfullSavedMessage();
                    loadAuctionsHouse();
                    break;
                case REQUEST_UPDATE_DELETE_AUCTION_HOUSE:
                    loadAuctionsHouse();
                    break;
            }
        }else{
            loadAuctionsHouse();
        }
    }

    private void loadAuctionsHouse() {
        new AuctionsHouseLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Casa de subastas guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditAuctionHouseActivity.class);
        startActivityForResult(intent, AddEditAuctionHouseActivity.REQUEST_ADD_AUCTION_HOUSE);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), AuctionHouseDetailActivity.class);
        intent.putExtra(AuctionsHouseActivity.EXTRA_AUCTION_HOUSE_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_AUCTION_HOUSE);
    }

    private class AuctionsHouseLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllAuctionsHouse ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mAuctionsHouseList.setVisibility (View.VISIBLE);
                    mAuctionsHouseAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mAuctionsHouseList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
