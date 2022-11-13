package com.example.catalogos.jewels_package.add_edit_jewel;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.database.mix_tables.JewelsGemstonesCutsContract;
import com.example.catalogos.database.mix_tables.JewelsGemstonesCutsContract.JewelsGemstonesCutsEntry;
import com.example.catalogos.designers_package.add_edit_designer.AddEditDesignerActivity;
import com.example.catalogos.designers_package.designers.DesignersCursorAdapter;
import com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import com.example.catalogos.dialog_package.SearchingDialog;
import com.example.catalogos.dialog_package.SearchingMultiDialog;
import com.example.catalogos.dialog_package.SearchingMultiDialogMultiColumn;
import com.example.catalogos.gemstones_cuts_package.gemstones_cuts_data.GemstonesCutsCursorAdapter;
import com.example.catalogos.gemstones_cuts_package.gemstones_cuts_data.JewelsGemstonesCuts;
import com.example.catalogos.gemstones_package.add_edit_gemstone.AddEditGemstoneActivity;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.hallmarks_package.add_edit_hallmark.AddEditHallmarkActivity;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarksCursorAdapter;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract;
import com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;
import com.example.catalogos.jewels_hallmarks_package.jewels_data.JewelsHallmarks;
import com.example.catalogos.jewels_owners_package.jewels_data.JewelsOwners;
import com.example.catalogos.jewels_package.jewels_data.Jewel;
import com.example.catalogos.jewels_package.jewels_data.JewelsContract.JewelEntry;
import com.example.catalogos.jeweltypes_package.add_edit_jeweltype.AddEditJewelTypeActivity;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesCursorAdapter;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.lots_package.lots.LotsCursorAdapter;
import com.example.catalogos.lots_package.lots_data.Lot;
import com.example.catalogos.owners_package.add_edit_owner.AddEditOwnerActivity;
import com.example.catalogos.owners_package.owners.OwnersCursorAdapter;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.example.catalogos.periods_package.add_edit_period.AddEditPeriodActivity;
import com.example.catalogos.periods_package.periods.PeriodsCursorAdapter;
import com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.ImageSaver;
import com.example.catalogos.services.SavePictureFromCamera;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.io.FilenameUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.catalogos.dialog_package.SearchingDialog.ADD_ITEM_TO_SINGLE_LIST_CODE;
import static com.example.catalogos.dialog_package.SearchingMultiDialog.ADD_ITEM_TO_MULTI_LIST_CODE;
import static com.example.catalogos.dialog_package.SearchingMultiDialogMultiColumn.ADD_ITEM_TO_MULTI_LIST_MULTI_COLUMN_CODE;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FOLDER;

/**
 * Vista para creación/edición de un joya
 */
public class AddEditJewelFragment extends Fragment {
    private static final String ARG_JEWEL_ID = "arg_jewel_id";
    private static final String ARG_FK_AUCTION_ID = "arg_fk_auction_id";
    private static final String ARG_LOT_NUMBER = "arr_lot_number";
//    private static final String ARG_FK_AUCTION_ID = "arg_fk_auction_id";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String DESIGNER_FK = "designer";
    private static final String JEWEL_TYPE_FK = "jewelType";
    private static final String PERIOD_FK = "period";
    private static final String OWNERS_FK = "owners";
    private static final String HALLMARKS_FK = "hallmarks";
    private static final String GEMSTONES_FK = "gemstones";
    private static final String LOTS_FK = "lots";


    private String mJewelId;
    private String mLotNumber;
    private int mFkAuctionId;
    private DbHelper dbHelper;
    private FloatingActionButton mSaveButton;
    private FloatingActionButton photoButton;
    private TextInputEditText mNameField;
    private TextInputEditText mSerialField;
    private TextInputEditText mDesignerField;
    private TextInputEditText mJewelTypeField;
    private TextInputEditText mPeriodField;
    private TextInputEditText mLotField;
    private TextView mGemstoneField;
    private TextView mOwnerField;
    private TextView mHallmarkField;
    private Button tvTitleGemstones;
    private Button tvTitleOwners;
    private Button tvTitleHallmarks;
    private TextInputEditText mObsField;

    private TextInputLayout mJewelTypeLabel;

    private ImageView mAvatar;

    Map <String,Integer> keysToReturn = new HashMap<> ();
    Map <String, ArrayList> keysMultiToReturn = new HashMap<> ();
    Map <String, ArrayList> textsMultiToReturn = new HashMap<> ();

    private ArrayList elementsSelected = new ArrayList<> ();

    private Bitmap imageBitmap;
    private String strURLAvatar;
    // adapters
    private JewelTypesCursorAdapter mJewelTypeAdapter;
    private DesignersCursorAdapter mDesignerAdapter;
    private PeriodsCursorAdapter mPeriodAdapter;
    private OwnersCursorAdapter mOwnerAdapter;
    private HallmarksCursorAdapter mHallmarkAdapter;
    private GemstonesCutsCursorAdapter gemstonesCutsCursorAdapter;
    private LotsCursorAdapter mLotAdapter;

    private LinearLayout llGemstones;
    private LinearLayout llOwners;
    private LinearLayout llHallmarks;
    private ArrayList<Integer> ownwersKeys;
    private int jewel_Id;
    private SearchingDialog searchingDialog;
    private SearchingMultiDialog searchingMultiDialog;
    private SearchingMultiDialogMultiColumn searchingMultiDialogMultiColumn;


    public AddEditJewelFragment() {
        // Required empty public constructor
    }

    public static AddEditJewelFragment newInstance(String jewelId, int fkAuctionId, String lotNumber) {
        AddEditJewelFragment fragment = new AddEditJewelFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_JEWEL_ID, jewelId);
        args.putString(ARG_LOT_NUMBER, lotNumber);
        args.putInt(ARG_FK_AUCTION_ID, fkAuctionId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keysToReturn.put (JEWEL_TYPE_FK,0);
        keysToReturn.put (DESIGNER_FK,0);
        keysToReturn.put (PERIOD_FK,0);
        keysToReturn.put (LOTS_FK,0);

        keysMultiToReturn.put (OWNERS_FK,new ArrayList<> ());
        textsMultiToReturn.put (OWNERS_FK,new ArrayList<> ());
        keysMultiToReturn.put (HALLMARKS_FK,new ArrayList<> ());
        textsMultiToReturn.put (HALLMARKS_FK,new ArrayList<> ());
        keysMultiToReturn.put (GEMSTONES_FK,new ArrayList<> ());
        textsMultiToReturn.put (GEMSTONES_FK,new ArrayList<> ());

        if (getArguments() != null) {
            mJewelId = getArguments().getString(ARG_JEWEL_ID);
            mLotNumber = getArguments().getString(ARG_LOT_NUMBER);
            mFkAuctionId = getArguments ().getInt (ARG_FK_AUCTION_ID);
        }
        if(savedInstanceState!= null){
            imageBitmap = savedInstanceState.getParcelable ("dataImage");
            keysToReturn = (Map<String, Integer>) savedInstanceState.get ("keysToReturn");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState (outState);
        outState.putParcelable ("dataImage",imageBitmap);
        outState.putSerializable ("keysToReturn", (Serializable) keysToReturn);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_jewel, container, false);

        dbHelper = new DbHelper (getActivity ());

        mJewelTypeAdapter = new JewelTypesCursorAdapter (getActivity(), null);
        mDesignerAdapter = new DesignersCursorAdapter (getActivity(), null);
        mPeriodAdapter = new PeriodsCursorAdapter (getActivity(), null);
        mLotAdapter = new LotsCursorAdapter (getActivity(), null);

        llGemstones = new LinearLayout (getActivity (),null);
        llOwners = new LinearLayout (getActivity (),null);
        llHallmarks = new LinearLayout (getActivity (),null);

        Cursor cursor = dbHelper.getAllJewelsGemstonesCuts ();
        gemstonesCutsCursorAdapter = new GemstonesCutsCursorAdapter (getActivity(), cursor);
        mOwnerAdapter = new OwnersCursorAdapter (getActivity(), null);
        mHallmarkAdapter = new HallmarksCursorAdapter (getActivity(), null);

        // Referencias UI
        mSaveButton = getActivity().findViewById(R.id.fab);
        photoButton = root.findViewById(R.id.photoBtn);

        mNameField = root.findViewById(R.id.et_name);
        mSerialField = root.findViewById(R.id.et_serial);
        mDesignerField = root.findViewById(R.id.et_designer);
        mJewelTypeField = root.findViewById(R.id.et_jewelType);
        mPeriodField = root.findViewById(R.id.et_period);
        mLotField = root.findViewById(R.id.et_lot);

        llGemstones = root.findViewById (R.id.ll_gemstones);
        llOwners = root.findViewById (R.id.ll_owners);
        llHallmarks = root.findViewById (R.id.ll_hallmarks);
        tvTitleGemstones = root.findViewById (R.id.tv_title_gemstones);
        tvTitleOwners = root.findViewById (R.id.tv_title_owners);
        tvTitleHallmarks = root.findViewById (R.id.tv_title_hallmarks);
        mGemstoneField = root.findViewById(R.id.l_gemstones);
        mOwnerField = root.findViewById(R.id.l_owners);
        mHallmarkField = root.findViewById(R.id.l_hallmarks);

        mObsField = root.findViewById(R.id.et_obs);

        mJewelTypeLabel = root.findViewById(R.id.til_jewelType);

        mAvatar = root.findViewById(R.id.iv_avatar);

        if(imageBitmap != null){
            mAvatar.setImageBitmap((Bitmap) imageBitmap);
        }

        // Eventos
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditJewel();
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        // Carga de datos
        if (mJewelId != null) {
            loadJewel();
        }

        mLotField.setOnFocusChangeListener(new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Editable s = mLotField.getText ();
                    if (lotExists (s))
                        showUpOptionMessage(s);
                    // code to execute when EditText loses focus
                }
            }
        });

        mDesignerField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener (mDesignerAdapter, DesignerEntry.TABLE_NAME,DesignerEntry.NAME,R.string.searching_designer,mDesignerField, DESIGNER_FK, AddEditDesignerActivity.class);
            }
        });

        mJewelTypeField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mJewelTypeAdapter, JewelTypeEntry.TABLE_NAME, JewelTypeEntry.NAME,R.string.searching_jewel_type,mJewelTypeField,JEWEL_TYPE_FK, AddEditJewelTypeActivity.class );
            }
        });

        mPeriodField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mPeriodAdapter, PeriodEntry.TABLE_NAME, PeriodEntry.NAME,R.string.searching_period,mPeriodField,PERIOD_FK, AddEditPeriodActivity.class );
            }
        });

        tvTitleOwners.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                mOwnerAdapter.setMultiSelector (true);
                onClickMultiListener(mOwnerAdapter, OwnerEntry.TABLE_NAME,
                        OwnerEntry.NAME,
                        R.string.searching_owners,mOwnerField,OWNERS_FK,
                        AddEditOwnerActivity.class,textsMultiToReturn.get (OWNERS_FK) );
//                ownersSelected = elementsSelected;
            }
        });

        tvTitleHallmarks.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                mHallmarkAdapter.setMultiSelector (true);
                onClickMultiListener(mHallmarkAdapter, HallmarkEntry.TABLE_NAME,
                        HallmarkEntry.NAME,
                        R.string.searching_hallmarks,mHallmarkField,HALLMARKS_FK,
                        AddEditHallmarkActivity.class,textsMultiToReturn.get (HALLMARKS_FK) );
//                hallmarksSelected = elementsSelected;
            }
        });

        tvTitleGemstones.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                gemstonesCutsCursorAdapter.setMultiSelector (true);
                onClickMultiColumnListener(gemstonesCutsCursorAdapter, JewelsGemstonesCutsEntry.GEMS_CUTS_VIEW_WITH_ID_NAME,
                        new String []{GemstoneEntry.NAME, CutEntry.NAME},
                        new String []{GemstoneEntry.ALIAS__ID, CutEntry.ALIAS__ID},
                        R.string.searching_gemstones,mGemstoneField,GEMSTONES_FK, textsMultiToReturn.get (GEMSTONES_FK), AddEditGemstoneActivity.class );
            }
        });

        return root;
    }   // Fin OnCreateView

    private void showUpOptionMessage(Editable s){

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getActivity ());
        alert.setTitle("El Lote " + s + " ya existe, ¿Desea agregar esta joya a ese lote?");


        alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mLotField.setText ("");
                mLotField.requestFocus ();
            }
        });

        AlertDialog alertD = alert.create ();
        alertD.getWindow ().setGravity (Gravity.TOP);
        alertD.show();
    }

    private boolean lotExists(Editable s){
        return dbHelper.lotNumberExists (s, mFkAuctionId);
    }

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
        if (requestCode == ADD_ITEM_TO_MULTI_LIST_CODE && resultCode == RESULT_OK) {
            searchingMultiDialog.swapCursor ();
        }
        if (requestCode == ADD_ITEM_TO_MULTI_LIST_MULTI_COLUMN_CODE && resultCode == RESULT_OK) {
            searchingMultiDialogMultiColumn.swapCursor ();
        }
    }

    private void onClickListener(CursorAdapter adapter, String tableName, String fieldName, int title, TextInputEditText tiet, String key, Class addEditActivityClass){
        // Creamos un diálogo de búsqueda y lo abrimos
        searchingDialog = new SearchingDialog (getActivity (), adapter, tableName, fieldName, title,addEditActivityClass);

        searchingDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {

            @Override
            public void onDismiss(DialogInterface dialog){
                tiet.setText (searchingDialog.textReturned);
                keysToReturn.put(key,searchingDialog.keyReturned);
            }
        });
    }

    private void onClickMultiListener(CursorAdapter cursorAdapter, String tableName, String fieldName, int title, TextView textView, String key, Class addEditActivityClass, ArrayList elementsSelected){
        // Creamos un diálogo de búsqueda y lo abrimos
        searchingMultiDialog = new SearchingMultiDialog (getActivity (), cursorAdapter, tableName, fieldName, title,addEditActivityClass,elementsSelected);

        searchingMultiDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {
            @Override
            public void onDismiss(DialogInterface dialog){
                textView.setText (getTextStream(searchingMultiDialog.textsReturned));
                keysMultiToReturn.put(key,searchingMultiDialog.keysReturned);
                textsMultiToReturn.put(key,searchingMultiDialog.textsReturned);
            }
        });
    }



    private void onClickMultiColumnListener(CursorAdapter cursorAdapter, String tableName, String[] fieldsName, String[] keyFieldsName, int title, TextView textView, String key, ArrayList itemsSelected, Class addEditActivityClass){
        // Creamos un diálogo de búsqueda y lo abrimos
        searchingMultiDialogMultiColumn = new SearchingMultiDialogMultiColumn (getActivity (), cursorAdapter, tableName, fieldsName,keyFieldsName, title,addEditActivityClass,itemsSelected,2);

        searchingMultiDialogMultiColumn.setOnDismissListener (new DialogInterface.OnDismissListener () {
            @Override
            public void onDismiss(DialogInterface dialog){
                ArrayList<String> textReturnedPrety = addTextToArray(searchingMultiDialogMultiColumn.textsReturned);
                textView.setText (getTextStream(textReturnedPrety));
                keysMultiToReturn.put(key,searchingMultiDialogMultiColumn.keysReturned);
                textsMultiToReturn.put(key,searchingMultiDialogMultiColumn.textsReturned);
//                itemsSelected = searchingMultiDialogMultiColumn.textsReturned;
            }
        });
    }

    private ArrayList<String> addTextToArray(ArrayList<String[]> textsReturned){
        ArrayList<String> text = new ArrayList<> ();
        for (int i = 0; i < textsReturned.size (); i++) {
            String newText = textsReturned.get (i)[0];
            if (textsReturned.get (i)[1] != null)
                newText += " talla " + textsReturned.get (i)[1];
            text.add (newText);
        }
        return text;
    }

    private String getTextStream(ArrayList<String> list){
        StringBuilder stream = new StringBuilder ();
        for(int i=0 ; i<list.size () ; i++){
            if (i > 0)
                stream.append ("\n");
            stream.append (list.get (i));
        }
        return stream.toString ();
    }

    private String getTextStreamFromArray(ArrayList<String[]> list){
        StringBuilder stream = new StringBuilder ();
        for(int i=0 ; i<list.size () ; i++){
            if (i > 0)
                stream.append ("\n");
            stream.append (list.get (i)[0]);
            if (list.get (i)[1] != null)
                stream.append (" talla ").append (list.get (i)[1]);
        }
        return stream.toString ();
    }

    private void loadJewel() {
        new GetJewelByIdTask().execute();
    }

    private void addEditJewel() {
        boolean error = false;

        String name = mNameField.getText().toString();
        String serial = mSerialField.getText().toString();
        String lot = mLotField.getText().toString();
        String jewelType = mJewelTypeField.getText().toString();
//        String designer = mDesignerField.getText().toString();
//        String period = mPeriodField.getText().toString();
        String obs = mObsField.getText().toString();


        if (TextUtils.isEmpty(jewelType)) {
            mJewelTypeLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        int jewelTypeFK = keysToReturn.get(JEWEL_TYPE_FK);
        int periodFK = keysToReturn.get(PERIOD_FK);
        int designerFK = keysToReturn.get(DESIGNER_FK);
//        ArrayList<Object> ownersFK = keysMultiToReturn.get(OWNERS_FK);

        String oldImageName;
        String newImageName = null;
        Jewel jewel = new Jewel (name, jewelTypeFK, periodFK,designerFK, mFkAuctionId, lot,serial,obs, newImageName);
        // se ha creado un nuevo Jewel con un ID diferente, Hay que actualizar el nombre de la imagen
        //save image
        if (imageBitmap != null) {   // Se ha hecho una nueva foto
            newImageName = jewel.getId () + ".jpg";
            // Guardamos la nueva foto
            new SavePictureFromCamera (imageBitmap, newImageName, JEWEL_FOLDER).save ();
            jewel.setAvatarUri (newImageName);
            if(strURLAvatar != null) {    // Ya existía una imagen
                // Obtenemos el nombre de la vieja imagen
                oldImageName = FilenameUtils.getName (strURLAvatar);
                // Eliminamos la antigua foto
                new ImageSaver (getActivity ()).setDirectory (JEWEL_FOLDER).setFileName (oldImageName).deleteFile ();
            }
        }else{  // No hay nueva foto
            if(strURLAvatar != null) {    // Ya existía una imagen
                // Obtenemos el nombre de la vieja imagen
                oldImageName = FilenameUtils.getName (strURLAvatar);
                // cambiamos el nombre de la antigua foto
                newImageName = jewel.getId () + ".jpg";
                new ImageSaver (getActivity ()).setDirectory (JEWEL_FOLDER).setFileName (oldImageName).renameFile (newImageName);
                jewel.setAvatarUri (newImageName);
            }
        }

        new AddEditJewelTask().execute(jewel);

    }

    private void showJewelsScreen(Boolean requery) {
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

    private void showJewel(Jewel jewel) {
        mNameField.setText(jewel.getName());
        mSerialField.setText(jewel.getSerial ());
        mDesignerField.setText(jewel.getDesigner ());
        mJewelTypeField.setText(jewel.getJewelType ());
        mPeriodField.setText(jewel.getPeriod ());
        mLotField.setText (jewel.getLot());

        mObsField.setText(jewel.getObs ());

        mOwnerField.setText (getTextStream (jewel.getOwners ()));
        mHallmarkField.setText (getTextStream (jewel.getHallmarks ()));
        mGemstoneField.setText (getTextStreamFromArray (jewel.getGemstonesAndCut ()));

        keysToReturn.put (JEWEL_TYPE_FK,jewel.getJewelTypeId ());
        keysToReturn.put (DESIGNER_FK,jewel.getDesignerId ());
        keysToReturn.put (PERIOD_FK,jewel.getPeriodId ());
// TODO: 02/04/2022
//        keysMultiToReturn.put (OWNERS_FK,jewel.getOwnerID ());

        if (jewel.getAvatarUri () == null) {
            if(imageBitmap != null)
                mAvatar.setImageBitmap (imageBitmap);
            else
                mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {

            strURLAvatar = JEWEL_FILE_PATH + jewel.getAvatarUri ();

            Uri uri = Uri.parse (strURLAvatar);

            mAvatar.setImageURI (uri);
        }
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar joya", Toast.LENGTH_SHORT).show();
    }

    private class GetJewelByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getJewelById(mJewelId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    Jewel jewel = new Jewel (cursor);
                    showJewel(jewel);
//                    ownersSelected = jewel.getOwners();
                    keysMultiToReturn.put (OWNERS_FK,jewel.getOwnersKeys());
                    textsMultiToReturn.put (OWNERS_FK,jewel.getOwners ());
                    keysMultiToReturn.put (HALLMARKS_FK,jewel.getHallmarksKeys());
                    textsMultiToReturn.put (HALLMARKS_FK,jewel.getHallmarks ());
                    keysMultiToReturn.put (GEMSTONES_FK,jewel.getGemstonesAndCutKeys ());
                    textsMultiToReturn.put (GEMSTONES_FK,jewel.getGemstonesAndCut ());
                    cursor.moveToFirst ();
                    jewel_Id = cursor.getInt (cursor.getColumnIndex(JewelEntry.ALIAS__ID));
                    mFkAuctionId = jewel.getAuctionId ();
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


    private class AddEditJewelTask extends AsyncTask<Jewel, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Jewel... jewels) {
            boolean result = false;
            if (mJewelId != null) {
                result = dbHelper.updateJewel(jewels[0], mJewelId) > 0;
            } else {
                result = dbHelper.saveJewel(jewels[0]) > 0;
                mJewelId = jewels[0].getId ();
            }
            if (result){
                if(jewel_Id == 0) {
                    Cursor c = dbHelper.getJewelById (mJewelId);
                    c.moveToFirst ();
                    jewel_Id = c.getInt (c.getColumnIndex (JewelEntry.ALIAS__ID));
                }
                dbHelper.deleteJewelsOwners (jewel_Id);
                ArrayList ownersKeys = keysMultiToReturn.get (OWNERS_FK);
                for (int i = 0; i < Objects.requireNonNull (ownersKeys).size (); i++) {
                    int ownerKey = (int) ownersKeys.get(i);
                    result = dbHelper.saveJewelsOwners (new JewelsOwners (jewel_Id, ownerKey)) > 0;
                    if(!result)
                        return result;
                }
                dbHelper.deleteJewelsHallmarks (jewel_Id);
                ArrayList hallmarksKeys = keysMultiToReturn.get (HALLMARKS_FK);
                for (int i = 0; i < Objects.requireNonNull (hallmarksKeys).size (); i++) {
                    int hallmarkKey = (int) hallmarksKeys.get(i);
                    result = dbHelper.saveJewelsHallmarks (new JewelsHallmarks (jewel_Id, hallmarkKey)) > 0;
                    if(!result)
                        return result;
                }
                dbHelper.deleteJewelsGemstonesCuts (jewel_Id);
                ArrayList<int[]> gemstonesCutsKeys;
                gemstonesCutsKeys = keysMultiToReturn.get (GEMSTONES_FK);
                for (int i = 0; i < Objects.requireNonNull (gemstonesCutsKeys).size (); i++) {
                    int gemstoneKey = gemstonesCutsKeys.get(i)[0];
                    int cutKey = gemstonesCutsKeys.get(i)[1];
                    result = dbHelper.saveJewelsGemstonesCuts (
                            new JewelsGemstonesCuts (jewel_Id, gemstoneKey, cutKey)) > 0;
                    if(!result)
                        return result;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Cursor c = dbHelper.getJewelById (mJewelId);
            c.moveToFirst ();
//            try {
//                Jewel newJewel = new Jewel (c);
//                jewel_Id = newJewel.get_id();
////                new AddLotTask().execute (result);
//            } catch (ParseException e) {
//                e.printStackTrace ();
//            }
            showJewelsScreen(result);
        }

    }


//    private class AddLotTask extends AsyncTask<Boolean, Void, Boolean> {
//        Boolean pResult = false;
//        @Override
//        protected Boolean doInBackground(Boolean... results){
//            if(results[0]){
//                Lot lot = new Lot (mLotField.getText ().toString (),mFkAuctionId,jewel_Id);
//                pResult = dbHelper.saveLot(lot) > 0;
//            }
//            return pResult;
//        }
//    }



}
