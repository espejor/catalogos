package com.example.catalogos.countries_package.add_edit_country;


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
import com.example.catalogos.countries_package.add_edit_country.AddEditCountryFragment;
import com.example.catalogos.countries_package.countries_data.Country;
import com.example.catalogos.countries_package.countries_data.Country;
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
import static com.example.catalogos.countries_package.countries_data.Country.COUNTRY_FILE_PATH;
import static com.example.catalogos.countries_package.countries_data.Country.COUNTRY_FOLDER;
import static com.example.catalogos.countries_package.countries_data.Country.COUNTRY_FILE_PATH;

/**
 * Vista para creación/edición de un país
 */
public class AddEditCountryFragment extends Fragment {
    private static final String ARG_COUNTRY_ID = "arg_country_id";
    private static final int REQUEST_SEARCH_PICTURE_FROM_WEB_CODE = 1;
    private static final int REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE = 2;

    private String mCountryId;

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


    public AddEditCountryFragment() {
        // Required empty public constructor
    }

    public static AddEditCountryFragment newInstance(String countryId) {
        AddEditCountryFragment fragment = new AddEditCountryFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY_ID, countryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCountryId = getArguments().getString(ARG_COUNTRY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_country, container, false);

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
                addEditCountry ();
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
        if (mCountryId != null) {
            loadCountry ();
        }

        return root;
    }

    private void openSearchPictures(){
        String textToSearch = "flag+" +  mNameField.getText ().toString();

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
//                        showImageCountryFromWebURI (new Country (name, null));
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

    private void loadCountry() {
        new AddEditCountryFragment.GetCountryByIdTask ().execute();
    }

    private void addEditCountry() {
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
        Country country = new Country (name,newImageName);        // se ha creado un nuevo Country con un ID diferente, Hay que actualizar el nombre de la imagen

        //  Si se ha descargado una imagen
        if (strURLImageFound != null){ // hay que crear una imagen en memoria
            String avatarExtension = FilenameUtils.getExtension (strURLImageFound);
            avatarExtension = avatarExtension.equals ("") ? "jpg" :avatarExtension;
            newImageName = country.getId () + "." + avatarExtension;
            country.setAvatarUri (newImageName);
            if(bitmap != null)
                new SavePictureFromURI (bitmap, country.getId (), COUNTRY_FOLDER).execute ();
            else
                new SavePictureFromURI (strURLImageFound, country.getId (), COUNTRY_FOLDER).execute ();
            if(existedPreviousImage())
                deleteImage(strURLAvatar);
        }else{
            if(existedPreviousImage()) {
                String avatarExtension = FilenameUtils.getExtension (strURLAvatar);
                newImageName = country.getId () + "." + avatarExtension;
                country.setAvatarUri (newImageName);
                renameImage (FilenameUtils.getName (strURLAvatar), newImageName);
            }
        }

        new AddEditCountryFragment.AddEditCountryTask ().execute(country);

    }

    private void renameImage(String oldImageName, String newImageName){
        new ImageSaver (getActivity ()).setDirectory (COUNTRY_FOLDER).setFileName (oldImageName).renameFile (newImageName);
    }

    private void deleteImage(String urlAvatar){
        new ImageSaver (getActivity ()).setDirectory (COUNTRY_FOLDER).setFileName (FilenameUtils.getName (strURLAvatar)).deleteFile ();
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

    private void showCountry(Country country) {
        mNameField.setText(country.getName());
        strURLAvatar = COUNTRY_FILE_PATH + country.getAvatarUri ();
        if (country.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (country.getAvatarUri ()))
                strURLAvatar = country.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar Ciudad", Toast.LENGTH_SHORT).show();
    }

    private class GetCountryByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getCountryById(mCountryId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showCountry (new Country (cursor));
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

    private class AddEditCountryTask extends AsyncTask<Country, Void, Boolean> {
        Country country;

        @Override
        protected Boolean doInBackground(Country... countries) {
            this.country = countries[0];
            if (mCountryId != null) {
                return dbHelper.updateCountry(countries[0], mCountryId) > 0;
            } else {
                return dbHelper.saveCountry(countries[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showAuctionsScreen(result);
        }

    }

}
