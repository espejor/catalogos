package com.example.catalogos.auctions_house_package.auction_house_detail;


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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.add_edit_auction_house.AddEditAuctionHouseActivity;
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseActivity;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseFragment;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.dialog_package.AlertDialogConfirmation;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.example.catalogos.services.ImageSaver;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.apache.commons.io.FilenameUtils;

import java.lang.reflect.Method;
import java.text.ParseException;

import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse.AUCTION_HOUSE_FILE_PATH;
import static com.example.catalogos.auctions_house_package.auctions_house_data.AuctionHouse.AUCTION_HOUSE_FOLDER;

/**
 * Vista para el detalle del subasta
 */
public class AuctionHouseDetailFragment extends Fragment {
    private static final String ARG_AUCTION_HOUSE_ID = "auctionHouseId";

    private String mAuctionHouseId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;
    private String avatarURI;


    public AuctionHouseDetailFragment() {
        // Required empty public constructor
    }

    public static AuctionHouseDetailFragment newInstance(String auctionHouseId) {
        AuctionHouseDetailFragment fragment = new AuctionHouseDetailFragment ();
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auction_house_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadAuctionHouse ();

        return root;
    }

    private void loadAuctionHouse() {
        new AuctionHouseDetailFragment.GetAuctionHouseByIdTask ().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manage_home:
                new GoHome (getActivity ()).execute ();
                return true;
            case R.id.action_edit:
                showEditScreen();
                break;
            case R.id.action_delete:
                try {
                    Method onConfirm = this.getClass().getMethod ("delAuctionHouseTask",(Class<?>[])null);
                    new AlertDialogConfirmation (getActivity (),this,"¿Está seguro de eliminar esta Subasta?\n¡Ojo! Se eliminarán todos los Catálogos de esta Casa de Subastas.\n¿Está de acuerdo?",onConfirm,null);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace ();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void delAuctionHouseTask(){
        new DeleteAuctionHouseTask ().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AuctionsHouseFragment.REQUEST_UPDATE_DELETE_AUCTION_HOUSE) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showAuctionHouse(AuctionHouse auctionHouse) {
        mCollapsingView.setTitle(auctionHouse.getName());
        if (auctionHouse.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (auctionHouse.getAvatarUri ()))
                avatarURI = auctionHouse.getAvatarUri ();
            else
                avatarURI = AUCTION_HOUSE_FILE_PATH + auctionHouse.getAvatarUri ();

            Uri uri = Uri.parse (avatarURI);

            mAvatar.setImageURI (uri);
        }
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditAuctionHouseActivity.class);
        intent.putExtra(AuctionsHouseActivity.EXTRA_AUCTION_HOUSE_ID, mAuctionHouseId);
        startActivityForResult(intent, AuctionsHouseFragment.REQUEST_UPDATE_DELETE_AUCTION_HOUSE);
    }

    private void showAuctionsHouseScreen(boolean requery) {
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
                "Error al eliminar Casa de Subastas", Toast.LENGTH_SHORT).show();
    }

    private class GetAuctionHouseByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAuctionHouseById (mAuctionHouseId);
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
            }
        }

    }

    private class DeleteAuctionHouseTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteAuctionHouse (mAuctionHouseId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(avatarURI != null)
                new ImageSaver (getActivity ()).setDirectory (AUCTION_HOUSE_FOLDER).setFileName (FilenameUtils.getName (avatarURI)).deleteFile ();

            showAuctionsHouseScreen(integer > 0);
        }

    }
}
