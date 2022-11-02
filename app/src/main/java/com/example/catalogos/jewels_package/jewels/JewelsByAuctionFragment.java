package com.example.catalogos.jewels_package.jewels;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.dialog_package.QueryDialog;
import com.example.catalogos.jewels_package.add_edit_jewel.AddEditJewelActivity;
import com.example.catalogos.jewels_package.jewel_detail.JewelDetailActivity;
import com.example.catalogos.lots_package.lots.JewelsByLotDetailActivity;
import com.example.catalogos.pdf_creator.PDFCreator;
import com.example.catalogos.services.pdfviewer.PDFViewActivity;
import com.example.catalogos.services.pdfviewer.SelectViewerForPDFDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_BUNDLE_SEARCH;
import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;
import static com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import static com.example.catalogos.services.pdfviewer.PDFViewActivity.PATH;

import java.io.File;
import java.lang.reflect.Method;


/**
 * Vista para la lista de joyas del gabinete
 */
public class JewelsByAuctionFragment extends Fragment{
    public static final int REQUEST_UPDATE_DELETE_JEWEL = 2;
    private static final String ARG_FK_AUCTION_ID = "fkAuctionId";

    private DbHelper mDbHelper;

    private ListView mJewelsList;
    private JewelsCursorAdapter mJewelsAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;
    private String mJewelId;
    private int mFkAuctionId;
    private Bundle mSearchJewelDataBundle;
    private Cursor cursor;
    private Cursor cursorForPDF;
    PDFCreator pdfCreator;


    public JewelsByAuctionFragment() {
        // Required empty public constructor
    }

    public static JewelsByAuctionFragment newInstance() {
        JewelsByAuctionFragment fragment = new JewelsByAuctionFragment ();
//        Bundle args = new Bundle();
//        args.putInt(ARG_FK_AUCTION_ID, fkAuctionId);
//        fragment.setArguments(args);
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate (savedInstanceState);
        if (getArguments() != null) {
            mFkAuctionId = getArguments ().getInt (EXTRA_FK_AUCTION_ID);
            mSearchJewelDataBundle = getArguments ().getBundle (EXTRA_BUNDLE_SEARCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jewels, container, false);

        // Referencias UI
        mJewelsList = root.findViewById(R.id.jewels_list);
        mJewelsAdapter = new JewelsCursorAdapter (getActivity(), null);

        mAddButton =  root.findViewById(R.id.fab3);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);


        if(mFkAuctionId == 0){
            mAddButton.setVisibility (View.INVISIBLE);
        }

        // Setup
        mJewelsList.setAdapter(mJewelsAdapter);

        // Eventos
        mJewelsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mJewelsAdapter.getItem(i);
                String currentJewelId = currentItem.getString(
                        currentItem.getColumnIndex(JewelEntry.ID));
                String currentLotNumber = currentItem.getString(
                        currentItem.getColumnIndex(JewelEntry.LOT));
                int currentFKAuctionId = currentItem.getInt(
                        currentItem.getColumnIndex(JewelEntry.FK_AUCTION_ID));

                if (mFkAuctionId != 0)
                    showDetailByLotScreen (currentJewelId,currentLotNumber,currentFKAuctionId);
                else
                    showDetailJewelScreen (currentJewelId);
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
        loadJewels();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditJewelActivity.REQUEST_ADD_JEWEL:
                    showSuccessfullSavedMessage();
                    loadJewels();
                    break;
                case REQUEST_UPDATE_DELETE_JEWEL:
                    loadJewels();
                    break;
            }
        }else{
            loadJewels ();
        }
    }

    private void loadJewels() {
        new JewelsLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Joya guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditJewelActivity.class);
        intent.putExtra(EXTRA_FK_AUCTION_ID, mFkAuctionId);
        startActivityForResult(intent, AddEditJewelActivity.REQUEST_ADD_JEWEL);
    }

    private void showDetailJewelScreen(String jewelId) {
        Intent intent = new Intent(getActivity(), JewelDetailActivity.class);
        intent.putExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID, jewelId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_JEWEL);
    }

    private void showDetailByLotScreen(String jewelId, String currentLotNumber, int currentFKAuctionId) {
        Intent intent = new Intent(getActivity(), JewelsByLotDetailActivity.class);
        intent.putExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID, jewelId);
        intent.putExtra(JewelsByAuctionActivity.EXTRA_LOT_NUMBER, currentLotNumber);
        int tempFkAuctionId = mFkAuctionId == 0 ? currentFKAuctionId : mFkAuctionId;
        intent.putExtra(EXTRA_FK_AUCTION_ID, tempFkAuctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_JEWEL);
    }

    public Cursor getCursor(){
        return cursor;
    }

    public void createPDF(){
        pdfCreator = new PDFCreator (getActivity ());
        if(mFkAuctionId != 0)
            cursorForPDF =  mDbHelper.getAllJewelsByAuctionId (mFkAuctionId,false);
        else{
            cursorForPDF = mDbHelper.getAllJewelsForList (mSearchJewelDataBundle,false);
        }

        try {
            Method onConfirm = this.getClass().getMethod ("printOnlyImages",(Class<?>[])null);
            Method onReject = this.getClass().getMethod ("printAllInfo",(Class<?>[])null);
            new QueryDialog (getActivity (),this,"¿Desea imprimir sólo las imágenes de las joyas?",onConfirm,onReject);
        } catch (NoSuchMethodException e) {
            e.printStackTrace ();
        }
    }

    public void printOnlyImages(){
        cursorForPDF = mDbHelper.getAllJewelsForList (mSearchJewelDataBundle,true);
        pdfCreator.createListOfImagesPDF (cursorForPDF);
        new SelectViewerForPDFDialog (pdfCreator.getFilePath (),pdfCreator.getPDFFile()).show(getActivity().getSupportFragmentManager(),"SelectViewerForPDFDialog");
    }

    public void printAllInfo(){
        pdfCreator.createListPDF (cursorForPDF);
        new SelectViewerForPDFDialog (pdfCreator.getFilePath (),pdfCreator.getPDFFile()).show(getActivity().getSupportFragmentManager(),"SelectViewerForPDFDialog");
    }

    public void onLocal(String filePath, File pdfFile){
        Intent intent = new Intent (getActivity (), PDFViewActivity.class);
        intent.putExtra (PATH,pdfCreator.getFilePath ());
        intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity ().startActivity (intent);

    }

    public void onExternal(String filePath, File pdfFile){
        if (pdfCreator.getPDFFile().exists()){
            //Uri uri = templatePDF.getUri ();
            Uri uri = pdfCreator.getUri ();
            Intent intent = new Intent (Intent.ACTION_VIEW)
                    .setDataAndType(uri,"application/pdf")
                    .setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    ;

            try {
                getActivity ().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                getActivity ().startActivity (new Intent (Intent.ACTION_VIEW,
                        Uri.parse ("market://details?id=com.adobe.reader")));
                Toast.makeText (getActivity (),"No cuentas con una aplicación para lectura de archivos PDF",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText (getActivity (),"No se encontró el archivo PDF",Toast.LENGTH_LONG).show();
        }
    }


    private class JewelsLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            if(mFkAuctionId != 0)
                return mDbHelper.getAllJewelsByAuctionId (mFkAuctionId,true);
            else{
                return mDbHelper.getAllJewelsForList (mSearchJewelDataBundle,true);
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mTextEmptyList.setVisibility (View.INVISIBLE);
                mJewelsList.setVisibility (View.VISIBLE);
                mJewelsAdapter.swapCursor(cursor);
                cursor.moveToFirst ();
                JewelsByAuctionFragment.this.cursor = cursor;
//                mJewelId = cursor.getString (cursor.getColumnIndex (JewelEntry.ID));
            } else {
                // Mostrar empty state
                mTextEmptyList.setVisibility (View.VISIBLE);
                mJewelsList.setVisibility (View.INVISIBLE);
            }
        }
    }



}
