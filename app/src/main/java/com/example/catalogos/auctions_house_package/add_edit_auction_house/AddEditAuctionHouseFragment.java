package com.example.catalogos.auctions_house_package.add_edit_auction_house;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.google_search.ItemListActivity;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.ImageSaver;
import com.example.catalogos.services.SavePictureFromURI;
import com.example.catalogos.xyz.App;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.FilenameUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static android.app.Activity.RESULT_OK;
import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse.AUCTION_HOUSE_FILE_PATH;
import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse.AUCTION_HOUSE_FOLDER;

/**
 * Vista para creación/edición de un casa de subasta
 */
public class AddEditAuctionHouseFragment extends Fragment {
    private static final String ARG_AUCTION_HOUSE_ID = "arg_auction_house_id";
    private static final int REQUEST_SEARCH_PICTURE_FROM_WEB_CODE = 1;
    private static final int REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE = 2;

    private String mAuctionHouseId;

    private DbHelper dbHelper;

    private FloatingActionButton mSaveButton;
    private TextInputEditText mNameField;
    private TextInputLayout mNameLabel;
    private Button btnSearchWebImg,btnSearchLocalImg;
    private ImageView mAvatar;
    private String strURLAvatar;
    private String strURLImageFound;
    private Bitmap bitmap;
//    private String strURLImageFromWeb;


    public AddEditAuctionHouseFragment() {
        // Required empty public constructor
    }

    public static AddEditAuctionHouseFragment newInstance(String auctionHouseId) {
        AddEditAuctionHouseFragment fragment = new AddEditAuctionHouseFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_AUCTION_HOUSE_ID, auctionHouseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAuctionHouseId = getArguments().getString(ARG_AUCTION_HOUSE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_auction_house, container, false);

        // Referencias UI
        mSaveButton = getActivity().findViewById(R.id.fab);
        mAvatar = root.findViewById(R.id.iv_avatar);
        mNameLabel = root.findViewById(R.id.til_name);
        mNameField = root.findViewById(R.id.et_name);

        btnSearchWebImg = root.findViewById(R.id.btn_search_web_img);
        btnSearchLocalImg = root.findViewById(R.id.btn_search_local_img);

        // Eventos
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditAuctionHouse ();
            }
        });

        btnSearchWebImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchPictures();
            }
        });

        btnSearchLocalImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchLocalPictures();
            }
        });

        dbHelper = new DbHelper(getActivity());

        // Carga de datos
        if (mAuctionHouseId != null) {
            loadAuctionHouse ();
        }

        return root;
    }

    private void openSearchPictures(){
        String textToSearch = "logo+casa+de+subastas+" +  mNameField.getText ().toString();

        Intent intent = new Intent(getActivity(), ItemListActivity.class);
        intent.putExtra(ItemListActivity.TEXT_TO_SEARCH, textToSearch);
        startActivityForResult(intent, REQUEST_SEARCH_PICTURE_FROM_WEB_CODE);
    }

    private void openSearchLocalPictures(){

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult (requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_SEARCH_PICTURE_FROM_WEB_CODE: {
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    strURLImageFound = data.getStringExtra ("link");
//                    String name = mNameField.getText ().toString ();
//                    String avatarExtension = FilenameUtils.getExtension (strURLImageFromWeb);

                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ()
                                .permitAll ().build ();
                        StrictMode.setThreadPolicy (policy);
//                        showImageAuctionHouseFromWebURI (new AuctionHouse (name, null));
                        Bitmap bitmap = new ImageSaver(App.getContext ()).makeBitmap (strURLImageFound);
                        mAvatar.setImageBitmap (bitmap);
                    }
                } else {
                    if (strURLAvatar == null) {
                        mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
                    }
                }
                break;
            }
            case REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE: {
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    Uri selectedImageUri = data.getData ();
                    strURLImageFound = selectedImageUri.toString ();
                    bitmap = new ImageSaver(App.getContext ()).makeBitmap (selectedImageUri);
                    mAvatar.setImageBitmap (bitmap);
//                    mAvatar.setImageURI (selectedImageUri);
                } else {
                    if (strURLAvatar == null) {
                        mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
                    }
                }
            }
        }
    }

    private void loadAuctionHouse() {
        new AddEditAuctionHouseFragment.GetAuctionHouseByIdTask ().execute();
    }

    private void addEditAuctionHouse() {
        boolean error = false;

        String name = mNameField.getText ().toString ();
        if (TextUtils.isEmpty(name)) {
            mNameLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        String newImageName = null;
        AuctionHouse auctionHouse = new AuctionHouse (name,newImageName);        // se ha creado un nuevo AuctionHouse con un ID diferente, Hay que actualizar el nombre de la imagen

        //  Si se ha descargado una imagen
        if (strURLImageFound != null){ // hay que crear una imagen en memoria
            String avatarExtension = FilenameUtils.getExtension (strURLImageFound);
            avatarExtension = avatarExtension.equals ("") ? "jpg" :avatarExtension;
            newImageName = auctionHouse.getId () + "." + avatarExtension;
            auctionHouse.setAvatarUri (newImageName);
            if(bitmap != null)
                new SavePictureFromURI (bitmap, auctionHouse.getId (), AUCTION_HOUSE_FOLDER).execute ();
            else
                new SavePictureFromURI (strURLImageFound, auctionHouse.getId (), AUCTION_HOUSE_FOLDER).execute ();
            if(existedPreviousImage())
                deleteImage(strURLAvatar);
        }else{
            if(existedPreviousImage()) {
                String avatarExtension = FilenameUtils.getExtension (strURLAvatar);
                newImageName = auctionHouse.getId () + "." + avatarExtension;
                auctionHouse.setAvatarUri (newImageName);
                renameImage (FilenameUtils.getName (strURLAvatar), newImageName);
            }
        }

        new AddEditAuctionHouseFragment.AddEditAuctionHouseTask ().execute(auctionHouse);

    }

    private void renameImage(String oldImageName, String newImageName){
        new ImageSaver (getActivity ()).setDirectory (AUCTION_HOUSE_FOLDER).setFileName (oldImageName).renameFile (newImageName);
    }

    private void deleteImage(String urlAvatar){
        new ImageSaver (getActivity ()).setDirectory (AUCTION_HOUSE_FOLDER).setFileName (FilenameUtils.getName (strURLAvatar)).deleteFile ();
    }

    private boolean existedPreviousImage(){
        String avatarName = FilenameUtils.getBaseName (strURLAvatar);
        return (avatarName != null && ! avatarName.equals ("null") && !avatarName.equals (""));
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

    private void showAuctionHouse(AuctionHouse auctionHouse) {
        mNameField.setText(auctionHouse.getName());
        strURLAvatar = AUCTION_HOUSE_FILE_PATH + auctionHouse.getAvatarUri ();
        if (auctionHouse.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (auctionHouse.getAvatarUri ()))
                strURLAvatar = auctionHouse.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar Casa de Subastas", Toast.LENGTH_SHORT).show();
    }

    private class GetAuctionHouseByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAuctionHouseById(mAuctionHouseId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showAuctionHouse (new AuctionHouse (cursor));
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

    private class AddEditAuctionHouseTask extends AsyncTask<AuctionHouse, Void, Boolean> {
        AuctionHouse auctionHouse;

        @Override
        protected Boolean doInBackground(AuctionHouse... auctionsHouse) {
            this.auctionHouse = auctionsHouse[0];
            if (mAuctionHouseId != null) {
                return dbHelper.updateAuctionHouse(auctionsHouse[0], mAuctionHouseId) > 0;
            } else {
                return dbHelper.saveAuctionHouse(auctionsHouse[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showAuctionsScreen(result);
        }

    }

}
