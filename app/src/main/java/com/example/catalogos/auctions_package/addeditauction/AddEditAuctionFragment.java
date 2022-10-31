package com.example.catalogos.auctions_package.addeditauction;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.catalogos.auctions_house_package.add_edit_auction_house.AddEditAuctionHouseActivity;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseCursorAdapter;
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract;
import com.example.catalogos.cities_package.add_edit_city.AddEditCityActivity;
import com.example.catalogos.cities_package.cities.CitiesCursorAdapter;
import com.example.catalogos.cities_package.cities_data.CitiesContract;
import com.example.catalogos.dialog_package.SearchingDialog;
import com.example.catalogos.services.DateInputMask;
import com.example.catalogos.services.ImageSaver;
import com.example.catalogos.services.SavePictureFromCamera;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.catalogos.R;
import com.example.catalogos.auctions_package.auctionsdata.Auction;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.services.DataConvert;

import org.apache.commons.io.FilenameUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.catalogos.auctions_package.auctionsdata.Auction.AUCTION_FILE_PATH;
import static com.example.catalogos.auctions_package.auctionsdata.Auction.AUCTION_FOLDER;
import static com.example.catalogos.dialog_package.SearchingDialog.ADD_ITEM_TO_SINGLE_LIST_CODE;
import static com.example.catalogos.dialog_package.SearchingMultiDialog.ADD_ITEM_TO_MULTI_LIST_CODE;

/**
 * Vista para creación/edición de un subasta
 */
public class AddEditAuctionFragment extends Fragment {
    private static final String ARG_AUCTION_ID = "arg_auction_id";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String AUCTION_HOUSE_FK = "auctionHouse";
    private static final String CITY_FK = "city";
    private static SimpleDateFormat formatSP = DataConvert.formatSP;

    private String mAuctionId;
    private DbHelper dbHelper;
    private FloatingActionButton mSaveButton;
    private FloatingActionButton photoButton;
    private TextInputEditText mNameField;
    private TextInputEditText mCityField;
    private TextInputEditText mAuctionHouseField;
    private TextInputEditText mDateField;
    private TextInputLayout mNameLabel;
    private TextInputLayout mCityLabel;
    private TextInputLayout mAuctionHouseLabel;
    private TextInputLayout mDateLabel;
    private CitiesCursorAdapter mCitiesAdapter;
    private AuctionsHouseCursorAdapter mAuctionsHouseAdapter;
    private ImageView mAvatar;

    Map <String,Integer> keysToReturn = new HashMap<> ();
    private Bitmap imageBitmap;
    private String strURLAvatar;
    SearchingDialog searchingDialog;


    public AddEditAuctionFragment() {
        // Required empty public constructor
    }

    public static AddEditAuctionFragment newInstance(String auctionId) {
        AddEditAuctionFragment fragment = new AddEditAuctionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_AUCTION_ID, auctionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuctionId = getArguments().getString(ARG_AUCTION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_auction, container, false);

        dbHelper = new DbHelper (getActivity ());

        mCitiesAdapter = new CitiesCursorAdapter (getActivity(), null);
        mAuctionsHouseAdapter = new AuctionsHouseCursorAdapter (getActivity(), null);
        // Referencias UI
        mSaveButton = getActivity().findViewById(R.id.fab);
        photoButton = root.findViewById(R.id.photoBtn);
        mNameField = root.findViewById(R.id.et_name);
        mCityField = root.findViewById(R.id.et_city);
        mAuctionHouseField = root.findViewById(R.id.et_auctionHouse);
        mDateField = root.findViewById(R.id.et_date);
        mNameLabel = root.findViewById(R.id.til_name);
        mCityLabel = root.findViewById(R.id.til_city);
        mAuctionHouseLabel = root.findViewById(R.id.til_auctionHouse);
        mDateLabel = root.findViewById(R.id.til_date);
        mAvatar = root.findViewById(R.id.iv_avatar);

        // Eventos
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditAuction();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        // Carga de datos
        if (mAuctionId != null) {
            loadAuction();
        }

        mCityField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener (mCitiesAdapter,CitiesContract.CityEntry.TABLE_NAME,CitiesContract.CityEntry.NAME,R.string.searching_city,mCityField, CITY_FK, AddEditCityActivity.class);
            }
        });


        mAuctionHouseField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mAuctionsHouseAdapter, AuctionsHouseContract.AuctionHouseEntry.TABLE_NAME, AuctionsHouseContract.AuctionHouseEntry.NAME,R.string.searching_auction_house,mAuctionHouseField,AUCTION_HOUSE_FK, AddEditAuctionHouseActivity.class );
            }
        });

        new DateInputMask (mDateField);

        return root;
    }   // Fin OnCreateView

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity ().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            mAvatar.setImageBitmap(imageBitmap);
        }
        if (requestCode == ADD_ITEM_TO_SINGLE_LIST_CODE && resultCode == RESULT_OK) {
            searchingDialog.swapCursor ();
        }
    }

    private void onClickListener(CursorAdapter adapter, String tableName, String fieldName, int title, TextInputEditText tiet, String key, Class addEditActivityClass){
        // Creamos un diálogo de búsqueda y lo abrimos
        searchingDialog = new SearchingDialog (getActivity (), adapter, tableName, fieldName, title,addEditActivityClass);

        searchingDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {
//            int ikey = key;

            @Override
            public void onDismiss(DialogInterface dialog){
                tiet.setText (searchingDialog.textReturned);
                keysToReturn.put(key,searchingDialog.keyReturned);
            }
        });
    }


    private class CitiesLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllCities ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
            if(cursor.getCount() > 0) {
                mCitiesAdapter.swapCursor(cursor);
            }
        }
    }

    private void loadAuction() {
        new GetAuctionByIdTask().execute();
    }

    private void addEditAuction() {
        boolean error = false;

        String name = mNameField.getText().toString();
        String city = mCityField.getText().toString();
        String auctionHouse = mAuctionHouseField.getText().toString();
        String dateRaw = mDateField.getText().toString();
        Date date = null;
        try {
            date = formatSP.parse (dateRaw);
        } catch (ParseException e) {
            mDateLabel.setError(getString(R.string.date_error));
            error = true;
            e.printStackTrace ();
        }

        if (TextUtils.isEmpty(name)) {
            mNameLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (TextUtils.isEmpty(city)) {
            mCityLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (TextUtils.isEmpty(auctionHouse)) {
            mAuctionHouseLabel.setError(getString(R.string.field_error));
            error = true;
        }


        if (TextUtils.isEmpty(mDateField.getText().toString())) {
            mDateLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        String oldImageName;
        String newImageName = null;
        Auction auction = new Auction(name, keysToReturn.get(AUCTION_HOUSE_FK), keysToReturn.get(CITY_FK), date, newImageName);
        // se ha creado un nuevo Auction con un ID diferente, Hay que actualizar el nombre de la imagen
        //save image
        if (imageBitmap != null) {   // Se ha hecho una nueva foto
            newImageName = auction.getId () + ".jpg";
            // Guardamos la nueva foto
            new SavePictureFromCamera (imageBitmap, newImageName, AUCTION_FOLDER).save ();
            auction.setAvatarUri (newImageName);
            if(strURLAvatar != null) {    // Ya existía una imagen
                // Obtenemos el nombre de la vieja imagen
                oldImageName = FilenameUtils.getName (strURLAvatar);
                // Eliminamos la antigua foto
                new ImageSaver (getActivity ()).setDirectory (AUCTION_FOLDER).setFileName (oldImageName).deleteFile ();
            }
        }else{  // No hay nueva foto
            if(strURLAvatar != null) {    // Ya existía una imagen
                // Obtenemos el nombre de la vieja imagen
                oldImageName = FilenameUtils.getName (strURLAvatar);
                // cambiamos el nombre de la antigua foto
                newImageName = auction.getId () + ".jpg";
                new ImageSaver (getActivity ()).setDirectory (AUCTION_FOLDER).setFileName (oldImageName).renameFile (newImageName);
                auction.setAvatarUri (newImageName);
            }
        }

        new AddEditAuctionTask().execute(auction);

    }

    private void showAuctionsScreen(Boolean requery) {
        if (!requery) {
            showAddEditError();
            getActivity().setResult(Activity.RESULT_CANCELED);
        } else {
            getActivity().setResult(RESULT_OK);
        }

        getActivity().finish();
    }

    private void showAddEditError() {
        Toast.makeText(getActivity(),
                "Error al agregar nueva información", Toast.LENGTH_SHORT).show();
    }

    private void showAuction(Auction auction) {
        mNameField.setText(auction.getName());
        mCityField.setText(auction.getCity ());
        mAuctionHouseField.setText(auction.getAuctionHouse ());
        mDateField.setText(String.format("%1$td-%1$tm-%1$tY",auction.getDate ()));
//        strURLAvatar = AUCTION_FILE_PATH + auction.getAvatarUri ();
        keysToReturn.put (AUCTION_HOUSE_FK,auction.getAuctionHouseId ());
        keysToReturn.put (CITY_FK,auction.getCityId ());

        if (auction.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {

            strURLAvatar = AUCTION_FILE_PATH + auction.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar subasta", Toast.LENGTH_SHORT).show();
    }

    private class GetAuctionByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAuctionById(mAuctionId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showAuction(new Auction(cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        }

    }

    private class AddEditAuctionTask extends AsyncTask<Auction, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Auction... auctions) {
            if (mAuctionId != null) {
                return dbHelper.updateAuction(auctions[0], mAuctionId) > 0;

            } else {
                return dbHelper.saveAuction(auctions[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showAuctionsScreen(result);
        }

    }


}
