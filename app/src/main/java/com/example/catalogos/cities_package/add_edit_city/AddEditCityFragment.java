package com.example.catalogos.cities_package.add_edit_city;


import android.app.Activity;
import android.content.DialogInterface;
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
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.cities_package.cities.CitiesCursorAdapter;
import com.example.catalogos.cities_package.cities_data.CitiesContract;
import com.example.catalogos.cities_package.cities_data.City;
import com.example.catalogos.countries_package.add_edit_country.AddEditCountryActivity;
import com.example.catalogos.countries_package.countries.CountriesCursorAdapter;
import com.example.catalogos.countries_package.countries_data.CountriesContract;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.dialog_package.SearchingDialog;
import com.example.catalogos.google_search.ItemListActivity;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.ImageSaver;
import com.example.catalogos.services.SavePictureFromURI;
import com.example.catalogos.xyz.App;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.FilenameUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.catalogos.cities_package.cities_data.City.CITY_FILE_PATH;
import static com.example.catalogos.cities_package.cities_data.City.CITY_FOLDER;

/**
 * Vista para creación/edición de un subasta
 */
public class AddEditCityFragment extends Fragment {
    private static final String ARG_CITY_ID = "arg_city_id";
    private static final int REQUEST_SEARCH_PICTURE_FROM_WEB_CODE = 1;
    private static final int REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE = 2;
    private static final String COUNTRY_FK = "country";

    private String mCityId;

    private DbHelper dbHelper;

    private FloatingActionButton mSaveButton;
    private TextInputEditText mNameField;
    private TextInputLayout mNameLabel,mCountryLabel;
    private TextInputEditText mCountryField;
    private Button btnSearchWebImg,btnSearchLocalImg;
    private ImageView mAvatar;
    private String strURLAvatar;
    private String strURLImageFound;
    private Bitmap bitmap;

    Map<String,Integer> keysToReturn = new HashMap<> ();

    private CountriesCursorAdapter mCountriesAdapter;
//    private String strURLImageFromWeb;


    public AddEditCityFragment() {
        // Required empty public constructor
    }

    public static AddEditCityFragment newInstance(String cityId) {
        AddEditCityFragment fragment = new AddEditCityFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCityId = getArguments().getString(ARG_CITY_ID);
        }
        if(savedInstanceState!= null){
            keysToReturn = (Map<String, Integer>) savedInstanceState.get ("keysToReturn");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState (outState);
        outState.putSerializable ("keysToReturn", (Serializable) keysToReturn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_city, container, false);

        // Referencias UI
        mSaveButton = getActivity().findViewById(R.id.fab);
        mAvatar = root.findViewById(R.id.iv_avatar);
        mNameLabel = root.findViewById(R.id.til_name);
        mCountryLabel = root.findViewById(R.id.til_country);
        mNameField = root.findViewById(R.id.et_name);
        mCountryField = root.findViewById(R.id.et_country);

        btnSearchWebImg = root.findViewById(R.id.btn_search_web_img);
        btnSearchLocalImg = root.findViewById(R.id.btn_search_local_img);

        // Eventos
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditCity ();
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


        mCountryField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener (mCountriesAdapter, CountriesContract.CountryEntry.TABLE_NAME,CountriesContract.CountryEntry.NAME,R.string.searching_country,mCountryField, COUNTRY_FK, AddEditCountryActivity.class);
            }
        });

        dbHelper = new DbHelper(getActivity());
        mCountriesAdapter = new CountriesCursorAdapter (getActivity(), null);

        // Carga de datos
        if (mCityId != null) {
            loadCity ();
        }

        return root;
    }


    private void onClickListener(CursorAdapter adapter, String tableName, String fieldName, int title, TextInputEditText tiet, String key, Class addEditActivityClass){
        // Creamos un diálogo de búsqueda y lo abrimos
        SearchingDialog searchingDialog = new SearchingDialog (getActivity (), adapter, tableName, fieldName, title,addEditActivityClass);

        searchingDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {
//            int ikey = key;

            @Override
            public void onDismiss(DialogInterface dialog){
                tiet.setText (searchingDialog.textReturned);
                keysToReturn.put(key,searchingDialog.keyReturned);
            }
        });
    }


    private void openSearchPictures(){
        String textToSearch = "monumento+principal+" +  mNameField.getText ().toString();

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
//                        showImageCityFromWebURI (new City (name, null));
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

    private void loadCity() {
        new GetCityByIdTask ().execute();
    }

    private void addEditCity() {
        boolean error = false;

        String name = mNameField.getText ().toString ();
        String country = mCountryField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameLabel.setError(getString(R.string.field_error));
            error = true;
        }
        if (TextUtils.isEmpty(country)) {
            mCountryLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        String newImageName = null;
        City city = new City (name,keysToReturn.get(COUNTRY_FK),newImageName);        // se ha creado un nuevo City con un ID diferente, Hay que actualizar el nombre de la imagen

        //  Si se ha descargado una imagen
        if (strURLImageFound != null){ // hay que crear una imagen en memoria
            String avatarExtension = FilenameUtils.getExtension (strURLImageFound);
            avatarExtension = avatarExtension.equals ("") ? "jpg" :avatarExtension;
            newImageName = city.getId () + "." + avatarExtension;
            city.setAvatarUri (newImageName);
            if(bitmap != null)
                new SavePictureFromURI (bitmap, city.getId (), CITY_FOLDER).execute ();
            else
                new SavePictureFromURI (strURLImageFound, city.getId (), CITY_FOLDER).execute ();
            if(existedPreviousImage())
                deleteImage(strURLAvatar);
        }else{
            if(existedPreviousImage()) {
                String avatarExtension = FilenameUtils.getExtension (strURLAvatar);
                newImageName = city.getId () + "." + avatarExtension;
                city.setAvatarUri (newImageName);
                renameImage (FilenameUtils.getName (strURLAvatar), newImageName);
            }
        }

        new AddEditCityTask ().execute(city);

    }

    private void renameImage(String oldImageName, String newImageName){
        new ImageSaver (getActivity ()).setDirectory (CITY_FOLDER).setFileName (oldImageName).renameFile (newImageName);
    }

    private void deleteImage(String urlAvatar){
        new ImageSaver (getActivity ()).setDirectory (CITY_FOLDER).setFileName (FilenameUtils.getName (strURLAvatar)).deleteFile ();
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

    private void showCity(City city) {
        mNameField.setText(city.getName());
        mCountryField.setText (city.getCountry ());
        strURLAvatar = CITY_FILE_PATH + city.getAvatarUri ();
        keysToReturn.put (COUNTRY_FK,city.getCountryId ());

        if (city.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (city.getAvatarUri ()))
                strURLAvatar = city.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar Ciudad", Toast.LENGTH_SHORT).show();
    }

    private class  GetCityByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getViewCityById(mCityId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showCity (new City (cursor));
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

    private class AddEditCityTask extends AsyncTask<City, Void, Boolean> {
        City city;

        @Override
        protected Boolean doInBackground(City... cities) {
            this.city = cities[0];
            if (mCityId != null) {
                return dbHelper.updateCity(cities[0], mCityId) > 0;
            } else {
                return dbHelper.saveCity(cities[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showAuctionsScreen(result);
        }

    }

}
