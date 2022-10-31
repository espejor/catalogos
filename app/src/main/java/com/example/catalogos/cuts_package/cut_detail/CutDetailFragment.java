package com.example.catalogos.cuts_package.cut_detail;


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
import com.example.catalogos.cuts_package.add_edit_cut.AddEditCutActivity;
import com.example.catalogos.cuts_package.cuts.CutsActivity;
import com.example.catalogos.cuts_package.cuts.CutsFragment;
import com.example.catalogos.cuts_package.cuts_data.Cut;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

import static com.example.catalogos.cuts_package.cuts_data.Cut.CUT_FILE_PATH;

/**
 * Vista para el detalle del subasta
 */
public class CutDetailFragment extends Fragment {
    private static final String ARG_CUT_ID = "cutId";

    private String mCutId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;


    public CutDetailFragment() {
        // Required empty public constructor
    }

    public static CutDetailFragment newInstance(String cutId) {
        CutDetailFragment fragment = new CutDetailFragment ();
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

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cut_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadCut ();

        return root;
    }

    private void loadCut() {
        new GetCutByIdTask ().execute();
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
                new DeleteCutTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CutsFragment.REQUEST_UPDATE_DELETE_CUT) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showCut(Cut cut) {
        mCollapsingView.setTitle(cut.getName());
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = CUT_FILE_PATH;
        if (cut.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (cut.getAvatarUri ()))
                uriStr = cut.getAvatarUri ();
            else
                uriStr = filePath + cut.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        //        Glide.with(this)
//                .load(Uri.parse(filePath + cuts.getAvatarUri ()))
//                .centerCrop()
//                .into(mAvatar);
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditCutActivity.class);
        intent.putExtra(CutsActivity.EXTRA_CUT_ID, mCutId);
        startActivityForResult(intent, CutsFragment.REQUEST_UPDATE_DELETE_CUT);
    }

    private void showCutsScreen(boolean requery) {
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
                "Error al eliminar Tipo de Talla", Toast.LENGTH_SHORT).show();
    }

    private class GetCutByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getCutById (mCutId);
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
            }
        }

    }

    private class DeleteCutTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteCut (mCutId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showCutsScreen(integer > 0);
        }

    }

}
