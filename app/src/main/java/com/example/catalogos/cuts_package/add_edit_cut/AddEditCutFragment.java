package com.example.catalogos.cuts_package.add_edit_cut;


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
import com.example.catalogos.cuts_package.add_edit_cut.AddEditCutFragment;
import com.example.catalogos.cuts_package.cuts_data.Cut;
import com.example.catalogos.cuts_package.cuts_data.Cut;
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
import static com.example.catalogos.cuts_package.cuts_data.Cut.CUT_FILE_PATH;
import static com.example.catalogos.cuts_package.cuts_data.Cut.CUT_FOLDER;

/**
 * Vista para creación/edición de un subasta
 */
public class AddEditCutFragment extends Fragment {
    private static final String ARG_CUT_ID = "arg_cut_id";
    private static final int REQUEST_SEARCH_PICTURE_FROM_WEB_CODE = 1;
    private static final int REQUEST_LOAD_IMAGE_FROM_MEMORY_CODE = 2;

    private String mCutId;

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


    public AddEditCutFragment() {
        // Required empty public constructor
    }

    public static AddEditCutFragment newInstance(String cutId) {
        AddEditCutFragment fragment = new AddEditCutFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_CUT_ID, cutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCutId = getArguments().getString(ARG_CUT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_cut, container, false);

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
                addEditCut ();
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
        if (mCutId != null) {
            loadCut ();
        }

        return root;
    }

    private void openSearchPictures(){
        String textToSearch = "talla+de+joya+" +  mNameField.getText ().toString();

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
//                        showImageCutFromWebURI (new Cut (name, null));
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

    private void loadCut() {
        new AddEditCutFragment.GetCutByIdTask ().execute();
    }

    private void addEditCut() {
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
        Cut cut = new Cut (name,newImageName);        // se ha creado un nuevo Cut con un ID diferente, Hay que actualizar el nombre de la imagen

        //  Si se ha descargado una imagen
        if (strURLImageFound != null){ // hay que crear una imagen en memoria
            String avatarExtension = FilenameUtils.getExtension (strURLImageFound);
            avatarExtension = avatarExtension.equals ("") ? "jpg" :avatarExtension;
            newImageName = cut.getId () + "." + avatarExtension;
            cut.setAvatarUri (newImageName);
            if(bitmap != null)
                new SavePictureFromURI (bitmap, cut.getId (), CUT_FOLDER).execute ();
            else
                new SavePictureFromURI (strURLImageFound, cut.getId (), CUT_FOLDER).execute ();
            if(existedPreviousImage())
                deleteImage(strURLAvatar);
        }else{
            if(existedPreviousImage()) {
                String avatarExtension = FilenameUtils.getExtension (strURLAvatar);
                newImageName = cut.getId () + "." + avatarExtension;
                cut.setAvatarUri (newImageName);
                renameImage (FilenameUtils.getName (strURLAvatar), newImageName);
            }
        }

        new AddEditCutFragment.AddEditCutTask ().execute(cut);

    }

    private void renameImage(String oldImageName, String newImageName){
        new ImageSaver (getActivity ()).setDirectory (CUT_FOLDER).setFileName (oldImageName).renameFile (newImageName);
    }

    private void deleteImage(String urlAvatar){
        new ImageSaver (getActivity ()).setDirectory (CUT_FOLDER).setFileName (FilenameUtils.getName (strURLAvatar)).deleteFile ();
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

    private void showCut(Cut cut) {
        mNameField.setText(cut.getName());
        strURLAvatar = CUT_FILE_PATH + cut.getAvatarUri ();
        if (cut.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (cut.getAvatarUri ()))
                strURLAvatar = cut.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar Talla", Toast.LENGTH_SHORT).show();
    }

    private class GetCutByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getCutById(mCutId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showCut (new Cut (cursor));
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

    private class AddEditCutTask extends AsyncTask<Cut, Void, Boolean> {
        Cut cut;

        @Override
        protected Boolean doInBackground(Cut... cuts) {
            this.cut = cuts[0];
            if (mCutId != null) {
                return dbHelper.updateCut(cuts[0], mCutId) > 0;
            } else {
                return dbHelper.saveCut(cuts[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            showAuctionsScreen(result);
        }

    }

}
