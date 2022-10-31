package com.example.catalogos.auctions_package.auctions;


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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.catalogos.R;
import com.example.catalogos.auctions_package.addeditauction.AddEditAuctionActivity;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.auctions_package.auctiondetail.AuctionDetailActivity;

import static com.example.catalogos.auctions_package.auctionsdata.AuctionsContract.AuctionEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class AuctionsFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_AUCTION = 2;
    public static final int REQUEST_OPEN_CATALOGUE = 3;

    private DbHelper mDbHelper;

    private ListView mAuctionsList;
    private AuctionsCursorAdapter mAuctionsAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public AuctionsFragment() {
        // Required empty public constructor
    }

    public static AuctionsFragment newInstance() {
        return new AuctionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auctions, container, false);

        // Referencias UI
        mAuctionsList = root.findViewById(R.id.auctions_list);
        mAuctionsAdapter = new AuctionsCursorAdapter(this.getContext (), null);

        mAddButton =  root.findViewById(R.id.fab2);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mAuctionsList.setAdapter(mAuctionsAdapter);

        // Eventos
        mAuctionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mAuctionsAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(AuctionEntry.ID));

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
        mDbHelper = new DbHelper (getActivity());

        // Carga de datos
        loadAuctions();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditAuctionActivity.REQUEST_ADD_AUCTION:
                    showSuccessfullSavedMessage();
                    loadAuctions();
                    break;
                case REQUEST_UPDATE_DELETE_AUCTION:
                    loadAuctions();
                    break;
                case REQUEST_OPEN_CATALOGUE:
//                    loadAuctions();
                    break;
            }
        }else{
            loadAuctions ();
        }
    }

    private void loadAuctions() {
        new AuctionsLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Subasta guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditAuctionActivity.class);
        startActivityForResult(intent, AddEditAuctionActivity.REQUEST_ADD_AUCTION);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), AuctionDetailActivity.class);
        intent.putExtra(AuctionsActivity.EXTRA_AUCTION_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_AUCTION);
    }

    private class AuctionsLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDbHelper.getAllAuctions();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mTextEmptyList.setVisibility (View.INVISIBLE);
                mAuctionsList.setVisibility (View.VISIBLE);
                mAuctionsAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                mTextEmptyList.setVisibility (View.VISIBLE);
                mAuctionsList.setVisibility (View.INVISIBLE);
            }
        }
    }



}
