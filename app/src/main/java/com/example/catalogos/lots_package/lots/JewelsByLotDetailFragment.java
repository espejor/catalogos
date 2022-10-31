package com.example.catalogos.lots_package.lots;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.dialog_package.AlertDialogConfirmation;
import com.example.catalogos.jewels_package.add_edit_jewel.AddEditJewelActivity;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionFragment;
import com.example.catalogos.jewels_package.jewels_data.JewelsContract;
import com.example.catalogos.services.ImageSaver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FOLDER;
import static com.example.catalogos.lots_package.lots.JewelsByLotDetailActivity.EXTRA_JEWEL_ID;


/**
 * Vista para la lista de joyas del gabinete
 */
public class JewelsByLotDetailFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_LOT = 2;
    private static final String ARG_AUCTION_ID = "auctionId";
    private static final String ARG_LOT = "lot";
    private static final String ARG_JEWEL_ID = "jewel_id";
    private static final String ARG_LOT_NUMBER = "lot_number";
    private static final String ARG_FK_AUCTION_ID = "fk_auction_id";

    private String mAuctionId;
    private String mLot;
    private DbHelper mDbHelper;

    private RecyclerView mLotsList;
    private RecyclerLotsCursorAdapter mLotsAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;
    private String jewelId;
    private String lotNumber;
    private int fkAuctionId;


    public JewelsByLotDetailFragment() {
        // Required empty public constructor
    }

    public static JewelsByLotDetailFragment newInstance(int fkAuctionId, String jewelId, String lotNumber) {
        JewelsByLotDetailFragment fragment = new JewelsByLotDetailFragment ();
        Bundle args = new Bundle();
        args.putInt (ARG_FK_AUCTION_ID, fkAuctionId);
        args.putString(ARG_JEWEL_ID, jewelId);
        args.putString(ARG_LOT_NUMBER, lotNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate (savedInstanceState);

        if (getArguments() != null) {
            mAuctionId = getArguments().getString(ARG_AUCTION_ID);
            fkAuctionId = getArguments().getInt (ARG_FK_AUCTION_ID);
            jewelId = getArguments().getString(ARG_JEWEL_ID);
            lotNumber = getArguments().getString(ARG_LOT_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_lots, container, false);

        // Referencias UI
        mLotsList = root.findViewById(R.id.lots_list);
        mLotsList.setLayoutManager (new LinearLayoutManager (getActivity (), LinearLayoutManager.HORIZONTAL, true));
        mLotsAdapter = new RecyclerLotsCursorAdapter (getActivity(), null);

        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        SnapHelper snapHelper = new LinearSnapHelper ( );
        snapHelper.attachToRecyclerView(mLotsList);

        // Setup
        mLotsList.setAdapter(mLotsAdapter);

        // Eventos
        mLotsList.addOnItemTouchListener (new RecyclerTouchListener(getActivity (), mLotsList, new RecyclerTouchListener.ClickListener () {
            @Override
            public void onClick(View view, int position){
//                Cursor currentItem = (Cursor) mLotsAdapter.getCurrentCursor ();
//                String currentJewelId = currentItem.getString(
//                        currentItem.getColumnIndex(JewelsContract.JewelEntry.ID));
//
//                showDetailScreen(currentJewelId);
            }

            @Override
            public void onLongClick(View view, int position){

            }
        }));

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                showDetailScreen(currentJewelId);
                showAddEditJewelScreen ();
            }
        });


//        getActivity().deleteDatabase(DbHelper.DATABASE_NAME);

        // Instancia de helper
        mDbHelper = new DbHelper (getActivity());

        // Carga de datos
        loadJewelsByLots ();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
//                case AddEditLotActivity.REQUEST_ADD_LOT:
//                    showSuccessfullSavedMessage();
//                    loadLots();
//                    break;
                case REQUEST_UPDATE_DELETE_LOT:
                    loadJewelsByLots ();
                    break;
            }
        }else{
            loadJewelsByLots ();
        }
    }

    private void loadJewelsByLots() {
        new JewelsByLotsLoadTask ().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Lote guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddEditJewelScreen() {

        Intent intent = new Intent(getActivity(), AddEditJewelActivity.class);
        intent.putExtra(EXTRA_JEWEL_ID, jewelId);
        intent.putExtra(ARG_LOT, mLot);
        intent.putExtra(EXTRA_FK_AUCTION_ID, fkAuctionId);
        startActivityForResult(intent, AddEditJewelActivity.REQUEST_ADD_JEWEL);
    }

    private void showDetailScreen(String JewelId) {
        Intent intent = new Intent(getActivity(), com.example.catalogos.jewels_package.jewel_detail.JewelDetailActivity.class);
        intent.putExtra(EXTRA_JEWEL_ID, JewelId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_LOT);
    }

    private class JewelsByLotsLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDbHelper.getJewelByLot (fkAuctionId, lotNumber, true);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mTextEmptyList.setVisibility (View.INVISIBLE);
                mLotsList.setVisibility (View.VISIBLE);
                mLotsAdapter.swapCursor(cursor);
                cursor.moveToFirst ();
            } else {
                // Mostrar empty state
                mTextEmptyList.setVisibility (View.VISIBLE);
                mLotsList.setVisibility (View.INVISIBLE);
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_edit:
//                showEditScreen();
//                break;
//            case R.id.action_delete:
//                new DeleteJewelTask ().execute();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void showEditScreen() {
        Cursor currentItem = (Cursor) mLotsAdapter.getCurrentCursor ();
        jewelId = currentItem.getString(
                currentItem.getColumnIndex(JewelsContract.JewelEntry.ID));
        Intent intent = new Intent(getActivity(), AddEditJewelActivity.class);
        intent.putExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID, jewelId);
        startActivityForResult(intent, JewelsByAuctionFragment.REQUEST_UPDATE_DELETE_JEWEL);
    }

    public void deleteJewel(){
        try {
            Method onConfirm = this.getClass().getMethod ("delTask",(Class<?>[])null);
            new AlertDialogConfirmation (getActivity (),this,"¿Está seguro de eliminar esta Joya?",onConfirm,null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace ();
        }
    }

    public void delTask(){
        new DeleteJewelTask ().execute();
    }


    public class DeleteJewelTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return mDbHelper.deleteJewel(jewelId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            String fileImageName = jewelId + ".jpg";
            new ImageSaver (getActivity ()).setDirectory (JEWEL_FOLDER).setFileName (fileImageName).deleteFile ();
            showJewelsScreen(integer > 0);
        }

    }

    private void showJewelsScreen(boolean requery) {
        if (!requery) {
            showDeleteError();
        }
        getActivity().setResult(requery ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
        getActivity().finish();
    }


    private void showDeleteError() {
        Toast.makeText(getActivity(),
                "Error al eliminar Joya", Toast.LENGTH_SHORT).show();
    }


}
