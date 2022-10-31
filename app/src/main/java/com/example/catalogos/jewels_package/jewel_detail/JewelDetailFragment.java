package com.example.catalogos.jewels_package.jewel_detail;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.jewels_package.add_edit_jewel.AddEditJewelActivity;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionFragment;
import com.example.catalogos.jewels_package.jewels_data.Jewel;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.ImageSaver;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;
import java.util.ArrayList;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FILE_PATH;
import static com.example.catalogos.jewels_package.jewels_data.Jewel.JEWEL_FOLDER;

/**
 * Vista para el detalle del joya
 */
public class JewelDetailFragment extends Fragment {
    private static final String ARG_JEWEL_ID = "jewelId";

    private String mJewelId;

    private CollapsingToolbarLayout mCollapsingView;
    
    private TextView mSerialField;
    private TextView mDesignerField;
    private TextView mJewelTypeField;
    private TextView mPeriodField;
    private TextView mObsField;
    
    private ImageView mAvatar;

    private DbHelper mDbHelper;

    private TextView mGemstoneField;
    private TextView mOwnerField;
    private int mFkAuctionId;


    public JewelDetailFragment() {
        // Required empty public constructor
    }

    public static JewelDetailFragment newInstance(String jewelId) {
        JewelDetailFragment fragment = new JewelDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_JEWEL_ID, jewelId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mJewelId = getArguments().getString(ARG_JEWEL_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jewel_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);

        mSerialField = root.findViewById(R.id.et_serial);
        mDesignerField = root.findViewById(R.id.et_designer);
        mJewelTypeField = root.findViewById(R.id.et_jewelType);
        mPeriodField = root.findViewById(R.id.et_period);
        mObsField = root.findViewById(R.id.et_obs);

        mGemstoneField = root.findViewById(R.id.l_gemstones);
        mOwnerField = root.findViewById(R.id.l_owners);

        
        mAvatar = getActivity().findViewById(R.id.iv_avatar);
        
        mDbHelper = new DbHelper (getActivity());

        loadJewel();

        return root;
    }

    private void loadJewel() {
        new GetJewelByIdTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                showEditScreen();
                break;
            case R.id.action_delete:
                new DeleteJewelTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JewelsByAuctionFragment.REQUEST_UPDATE_DELETE_JEWEL) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showJewel(Jewel jewel) {
        String filePath = JEWEL_FILE_PATH;
        if (jewel.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (jewel.getAvatarUri ()))
                uriStr = jewel.getAvatarUri ();
            else
                uriStr = filePath + jewel.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        
        mCollapsingView.setTitle("Lote: " + jewel.getLot ());
        mSerialField.setText(jewel.getSerial ());
        mDesignerField.setText(jewel.getDesigner ());
        mJewelTypeField.setText(jewel.getJewelType ());
        mPeriodField.setText(jewel.getPeriod ());
        mObsField.setText(jewel.getObs ());

        mOwnerField.setText (getTextStream (jewel.getOwners ()));
        mGemstoneField.setText (getTextStreamFromArray (jewel.getGemstonesAndCut ()));

        mFkAuctionId = jewel.getAuctionId ();

    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditJewelActivity.class);
        intent.putExtra(JewelsByAuctionActivity.EXTRA_JEWEL_ID, mJewelId);
        intent.putExtra(EXTRA_FK_AUCTION_ID, mFkAuctionId);
        startActivityForResult(intent, JewelsByAuctionFragment.REQUEST_UPDATE_DELETE_JEWEL);
    }

    private void showJewelsScreen(boolean requery) {
        if (!requery) {
            showDeleteError();
        }
        getActivity().setResult(requery ? Activity.RESULT_OK : Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al cargar información", Toast.LENGTH_SHORT).show();
    }

    private void showDeleteError() {
        Toast.makeText(getActivity(),
                "Error al eliminar Joya", Toast.LENGTH_SHORT).show();
    }

    private class GetJewelByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDbHelper.getJewelById(mJewelId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showJewel(new Jewel (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private String getTextStream(ArrayList<String> list){
        String stream = "";
        for(int i=0 ; i<list.size () ; i++){
            if (i > 0)
                stream += "\n";
            stream += list.get(i);
        }
        return stream;
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


    private class DeleteJewelTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return mDbHelper.deleteJewel(mJewelId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            String fileImageName = mJewelId + ".jpg";
            new ImageSaver (getActivity ()).setDirectory (JEWEL_FOLDER).setFileName (fileImageName).deleteFile ();
            showJewelsScreen(integer > 0);
        }

    }

}
