package com.example.catalogos.hallmarks_package.hallmark_detail;


import static com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark.HALLMARK_FILE_PATH;

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
import com.example.catalogos.hallmarks_package.add_edit_hallmark.AddEditHallmarkActivity;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarksFragment;
import com.example.catalogos.hallmarks_package.hallmarks_data.Hallmark;
import com.example.catalogos.hallmarks_package.hallmarks.HallmarksActivity;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

/**
 * Vista para el detalle del subasta
 */
public class HallmarkDetailFragment extends Fragment {
    private static final String ARG_HALLMARK_ID = "hallmarkId";

    private String mHallmarkId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;


    public HallmarkDetailFragment() {
        // Required empty public constructor
    }

    public static HallmarkDetailFragment newInstance(String hallmarkId) {
        HallmarkDetailFragment fragment = new HallmarkDetailFragment ();
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hallmark_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadHallmark ();

        return root;
    }

    private void loadHallmark() {
        new GetHallmarkByIdTask ().execute();
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
                new DeleteHallmarkTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HallmarksFragment.REQUEST_UPDATE_DELETE_HALLMARK) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showHallmark(Hallmark hallmark) {
        mCollapsingView.setTitle(hallmark.getName());

        String filePath = HALLMARK_FILE_PATH;
        if (hallmark.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (hallmark.getAvatarUri ()))
                uriStr = hallmark.getAvatarUri ();
            else
                uriStr = filePath + hallmark.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        //        Glide.with(this)
//                .load(Uri.parse(filePath + hallmarks.getAvatarUri ()))
//                .centerCrop()
//                .into(mAvatar);
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditHallmarkActivity.class);
        intent.putExtra(HallmarksActivity.EXTRA_HALLMARK_ID, mHallmarkId);
        startActivityForResult(intent, HallmarksFragment.REQUEST_UPDATE_DELETE_HALLMARK);
    }

    private void showHallmarksScreen(boolean requery) {
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
                "Error al eliminar Propietario", Toast.LENGTH_SHORT).show();
    }

    private class GetHallmarkByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getHallmarkById (mHallmarkId);
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
            }
        }

    }

    private class DeleteHallmarkTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteHallmark (mHallmarkId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showHallmarksScreen(integer > 0);
        }

    }

}
