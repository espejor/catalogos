package com.example.catalogos.auctions_package.auctiondetail;


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

import com.example.catalogos.dialog_package.AlertDialogConfirmation;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.example.catalogos.services.ImageSaver;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.example.catalogos.R;
import com.example.catalogos.auctions_package.addeditauction.AddEditAuctionActivity;
import com.example.catalogos.auctions_package.auctionsdata.Auction;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.auctions_package.auctions.AuctionsActivity;
import com.example.catalogos.auctions_package.auctions.AuctionsFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;
import java.text.ParseException;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_FK_AUCTION_ID;
import static com.example.catalogos.auctions_package.auctionsdata.Auction.AUCTION_FILE_PATH;
import static com.example.catalogos.auctions_package.auctionsdata.Auction.AUCTION_FOLDER;

/**
 * Vista para el detalle del subasta
 */
public class AuctionDetailFragment extends Fragment {
    private static final String ARG_AUCTION_ID = "auctionId";
    private static final String ARG_FK_AUCTION_ID = "fk_auctionId";

    private String mAuctionId;
    private int fkAuctionId;

    private FloatingActionButton jewelButton;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;
    private TextView mCity;
    private TextView mAuctionHouse;
    private TextView mDate;

    private DbHelper mDbHelper;


    public AuctionDetailFragment() {
        // Required empty public constructor
    }

    public static AuctionDetailFragment newInstance(String auctionId) {
        AuctionDetailFragment fragment = new AuctionDetailFragment();
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auction_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);
        mCity = root.findViewById(R.id.et_city);
        mAuctionHouse = root.findViewById(R.id.et_auctionHouse);
        mDate = root.findViewById(R.id.et_date);

        jewelButton = root.findViewById(R.id.jewelBtn);

        mDbHelper = new DbHelper (getActivity());

        jewelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAuctionCatalogue ();
            }
        });

        loadAuction();

        return root;
    }

    private void openAuctionCatalogue(){
        Intent intent = new Intent(getActivity(), JewelsByAuctionActivity.class);
        intent.putExtra(EXTRA_FK_AUCTION_ID, fkAuctionId);
        startActivityForResult(intent, AuctionsFragment.REQUEST_OPEN_CATALOGUE);
    }

    private void loadAuction() {
        new GetAuctionByIdTask().execute();
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
                    Method onConfirm = this.getClass().getMethod ("delAuctionTask",(Class<?>[])null);
                    new AlertDialogConfirmation (getActivity (),this,"¿Está seguro de eliminar esta Subasta?\n¡Ojo! Se eliminarán todas las joyas de esta Subasta.\n¿Está de acuerdo?",onConfirm,null);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace ();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void delAuctionTask(){
        new DeleteAuctionTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AuctionsFragment.REQUEST_UPDATE_DELETE_AUCTION) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showAuction(Auction auction) {
        String filePath = AUCTION_FILE_PATH;
        if (auction.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (auction.getAvatarUri ()))
                uriStr = auction.getAvatarUri ();
            else
                uriStr = filePath + auction.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        mCollapsingView.setTitle(auction.getName());
//        Glide.with(this)
//                .load(Uri.parse("file:///android_asset/" + auction.getAvatarUri()))
//                .centerCrop()
//                .into(mAvatar);
        mCity.setText(auction.getCity ());
        mAuctionHouse.setText(auction.getAuctionHouse ());
        mDate.setText(String.format("%1$td-%1$tm-%1$tY",auction.getDate ()));

    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditAuctionActivity.class);
        intent.putExtra(AuctionsActivity.EXTRA_AUCTION_ID, mAuctionId);
        startActivityForResult(intent, AuctionsFragment.REQUEST_UPDATE_DELETE_AUCTION);
    }

    private void showAuctionsScreen(boolean requery) {
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
                "Error al eliminar Subasta", Toast.LENGTH_SHORT).show();
    }

    private class GetAuctionByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDbHelper.getAuctionById(mAuctionId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    Auction auction = new Auction(cursor);
                    fkAuctionId = auction.get_id ();
                    showAuction(auction);
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteAuctionTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return mDbHelper.deleteAuction(mAuctionId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            String fileImageName = mAuctionId + ".jpg";
            new ImageSaver (getActivity ()).setDirectory (AUCTION_FOLDER).setFileName (fileImageName).deleteFile ();
            showAuctionsScreen(integer > 0);
        }

    }

}
