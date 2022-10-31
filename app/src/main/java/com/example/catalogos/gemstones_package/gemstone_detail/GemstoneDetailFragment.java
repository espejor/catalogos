package com.example.catalogos.gemstones_package.gemstone_detail;


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
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.gemstones_package.add_edit_gemstone.AddEditGemstoneActivity;
import com.example.catalogos.gemstones_package.gemstone_detail.GemstoneDetailFragment;
import com.example.catalogos.gemstones_package.gemstones.GemstonesActivity;
import com.example.catalogos.gemstones_package.gemstones.GemstonesFragment;
import com.example.catalogos.gemstones_package.gemstones_data.Gemstone;
import com.example.catalogos.gemstones_package.add_edit_gemstone.AddEditGemstoneActivity;
import com.example.catalogos.gemstones_package.gemstones.GemstonesActivity;
import com.example.catalogos.gemstones_package.gemstones.GemstonesFragment;
import com.example.catalogos.gemstones_package.gemstones_data.Gemstone;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

import static com.example.catalogos.gemstones_package.gemstones_data.Gemstone.GEMSTONE_FILE_PATH;
import static com.example.catalogos.gemstones_package.gemstones_data.Gemstone.GEMSTONE_FILE_PATH;

/**
 * Vista para el detalle del subasta
 */
public class GemstoneDetailFragment extends Fragment {
    private static final String ARG_GEMSTONE_ID = "gemstoneId";

    private String mGemstoneId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;


    public GemstoneDetailFragment() {
        // Required empty public constructor
    }

    public static GemstoneDetailFragment newInstance(String gemstoneId) {
        GemstoneDetailFragment fragment = new GemstoneDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_GEMSTONE_ID, gemstoneId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mGemstoneId = getArguments().getString(ARG_GEMSTONE_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gemstone_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadGemstone ();

        return root;
    }

    private void loadGemstone() {
        new GemstoneDetailFragment.GetGemstoneByIdTask ().execute();
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
                new GemstoneDetailFragment.DeleteGemstoneTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GemstonesFragment.REQUEST_UPDATE_DELETE_GEMSTONE) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showGemstone(Gemstone gemstone) {
        mCollapsingView.setTitle(gemstone.getName());
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = GEMSTONE_FILE_PATH;
        if (gemstone.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (gemstone.getAvatarUri ()))
                uriStr = gemstone.getAvatarUri ();
            else
                uriStr = filePath + gemstone.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        //        Glide.with(this)
//                .load(Uri.parse(filePath + gemstones.getAvatarUri ()))
//                .centerCrop()
//                .into(mAvatar);
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditGemstoneActivity.class);
        intent.putExtra(GemstonesActivity.EXTRA_GEMSTONE_ID, mGemstoneId);
        startActivityForResult(intent, GemstonesFragment.REQUEST_UPDATE_DELETE_GEMSTONE);
    }

    private void showGemstonesScreen(boolean requery) {
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
                "Error al eliminar Piedra Preciosa", Toast.LENGTH_SHORT).show();
    }

    private class GetGemstoneByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getGemstoneById (mGemstoneId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showGemstone (new Gemstone (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteGemstoneTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteGemstone (mGemstoneId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showGemstonesScreen(integer > 0);
        }

    }

}
