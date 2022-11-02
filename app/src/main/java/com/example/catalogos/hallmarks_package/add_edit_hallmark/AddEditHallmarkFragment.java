package com.example.catalogos.hallmarks_package.add_edit_hallmark;


import static android.app.Activity.RESULT_OK;
import static com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark.HALLMARK_FILE_PATH;
import static com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark.HALLMARK_FOLDER;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.countries_package.add_edit_country.AddEditCountryActivity;
import com.example.catalogos.countries_package.countries.CountriesCursorAdapter;
import com.example.catalogos.countries_package.countries_data.CountriesContract;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.dialog_package.SearchingArrayDialog;
import com.example.catalogos.dialog_package.SearchingDialog;
import com.example.catalogos.google_search.ItemListActivity;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarkTypeArrayAdapter;
import com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarkType;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.ImageSaver;
import com.example.catalogos.services.SavePictureFromURI;
import com.example.catalogos.xyz.App;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.FilenameUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Vista para creación/edición de un subasta
 */
public class AddEditHallmarkFragment extends Fragment {
    private static final String ARG_HALLMARK_ID = "arg_hallmark_id";
    private static final int REQUEST_SEARCH_PICTURE_FROM_WEB_CODE = 1;
    private static final int REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE = 2;

    Map<String,Integer> keysToReturn = new HashMap<> ();

    private ArrayAdapter<String> mHallmarkTypeAdapter;

    private String mHallmarkId;

    private DbHelper dbHelper;

    private FloatingActionButton mSaveButton;
    private TextInputEditText mNameField;
//    private TextInputEditText mHallmarkTypeField;
    private TextInputLayout mNameLabel;
//    private TextInputLayout mHallmarkTypeLabel;
    private Button btnSearchWebImg,btnSearchLocalImg;
    private ImageView mAvatar;
    private String strURLAvatar;
    private String strURLImageFound;
    private Bitmap bitmap;
//    private String strURLImageFromWeb;


    public AddEditHallmarkFragment() {
        // Required empty public constructor
    }

    public static AddEditHallmarkFragment newInstance(String hallmarkId) {
        AddEditHallmarkFragment fragment = new AddEditHallmarkFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_HALLMARK_ID, hallmarkId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHallmarkId = getArguments().getString(ARG_HALLMARK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_hallmark, container, false);

        // Referencias UI
        mSaveButton = getActivity().findViewById(R.id.fab);
        mAvatar = root.findViewById(R.id.iv_avatar);
        mNameLabel = root.findViewById(R.id.til_name);
        mNameField = root.findViewById(R.id.et_name);
//        mHallmarkTypeLabel = root.findViewById(R.id.til_hallmark_type);
//        mHallmarkTypeField = root.findViewById(R.id.et_hallmark_type);

        btnSearchWebImg = root.findViewById(R.id.btn_search_web_img);
        btnSearchLocalImg = root.findViewById(R.id.btn_search_local_img);

        // Eventos
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditHallmark ();
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

//
//        mHallmarkTypeField.setOnClickListener (new View.OnClickListener () {
//            @Override
//            public void onClick(View v){
//                onClickListener (mHallmarkTypeAdapter,R.string.searching_hallmark_type, mHallmarkTypeField);
//            }
//        });

        dbHelper = new DbHelper(getActivity());

        Set<String> listSet = new HallmarkType (getActivity ()).getListOfHallmarksTypes ();
        String[] listArray = listSet.toArray (new String[listSet.size ()]);
        mHallmarkTypeAdapter =
                new ArrayAdapter<>(getActivity (), android.R.layout.simple_list_item_1, listArray);


        // Carga de datos
        if (mHallmarkId != null) {
            loadHallmark ();
        }

        return root;
    }


    private void onClickListener(ArrayAdapter<String> adapter, int title, TextInputEditText tiet){
        // Creamos un diálogo de búsqueda y lo abrimos
        SearchingArrayDialog searchingArrayDialog = new SearchingArrayDialog (getActivity (), adapter, title);

        searchingArrayDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {
//            int ikey = key;

            @Override
            public void onDismiss(DialogInterface dialog){
//                tiet.setText (searchingArrayDialog.textReturned);
            }
        });
    }


    private void openSearchPictures(){
        String textToSearch = "hallmark+" +  mNameField.getText ().toString();

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
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ()
                                .permitAll ().build ();
                        StrictMode.setThreadPolicy (policy);
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

    private void loadHallmark() {
        new GetHallmarkByIdTask ().execute();
    }

    private void addEditHallmark() {
        boolean error = false;

        String name = mNameField.getText ().toString ();
        if (TextUtils.isEmpty(name)) {
            mNameLabel.setError(getString(R.string.field_error));
            error = true;
        }
//
//        String hallmarkType = mHallmarkTypeField.getText ().toString ();
//        if (TextUtils.isEmpty(hallmarkType)) {
//            mHallmarkTypeLabel.setError(getString(R.string.field_error));
//            error = true;
//        }

        if (error) {
            return;
        }

        String newImageName = null;
        Hallmark hallmark = new Hallmark (name,newImageName);        // se ha creado un nuevo Hallmark con un ID diferente, Hay que actualizar el nombre de la imagen

        //  Si se ha descargado una imagen
        if (strURLImageFound != null){ // hay que crear una imagen en memoria
            String avatarExtension = FilenameUtils.getExtension (strURLImageFound);
            avatarExtension = avatarExtension.equals ("") ? "jpg" :avatarExtension;
            newImageName = hallmark.getId () + "." + avatarExtension;
            hallmark.setAvatarUri (newImageName);
            if(bitmap != null)
                new SavePictureFromURI (bitmap, hallmark.getId (), HALLMARK_FOLDER).execute ();
            else
                new SavePictureFromURI (strURLImageFound, hallmark.getId (), HALLMARK_FOLDER).execute ();
            if(existedPreviousImage())
                deleteImage(strURLAvatar);
        }else{
            if(existedPreviousImage()) {
                String avatarExtension = FilenameUtils.getExtension (strURLAvatar);
                newImageName = hallmark.getId () + "." + avatarExtension;
                hallmark.setAvatarUri (newImageName);
                renameImage (FilenameUtils.getName (strURLAvatar), newImageName);
            }
        }

        new AddEditHallmarkTask ().execute(hallmark);

    }

    private void renameImage(String oldImageName, String newImageName){
        new ImageSaver (getActivity ()).setDirectory (HALLMARK_FOLDER).setFileName (oldImageName).renameFile (newImageName);
    }

    private void deleteImage(String urlAvatar){
        new ImageSaver (getActivity ()).setDirectory (HALLMARK_FOLDER).setFileName (FilenameUtils.getName (strURLAvatar)).deleteFile ();
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

    private void showHallmark(Hallmark hallmark) {
        mNameField.setText(hallmark.getName());
//        mHallmarkTypeField.setText(hallmark.getHallmarkType ());
        strURLAvatar = HALLMARK_FILE_PATH + hallmark.getAvatarUri ();
        if (hallmark.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (hallmark.getAvatarUri ()))
                strURLAvatar = hallmark.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar Propietario", Toast.LENGTH_SHORT).show();
    }

    private class GetHallmarkByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getHallmarkById(mHallmarkId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showHallmark (new Hallmark (cursor));
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

    private class AddEditHallmarkTask extends AsyncTask<Hallmark, Void, Boolean> {
        Hallmark hallmark;

        @Override
        protected Boolean doInBackground(Hallmark... hallmarks) {
            this.hallmark = hallmarks[0];
            if (mHallmarkId != null) {
                return dbHelper.updateHallmark(hallmarks[0], mHallmarkId) > 0;
            } else {
                return dbHelper.saveHallmark(hallmarks[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showAuctionsScreen(result);
        }

    }

}
