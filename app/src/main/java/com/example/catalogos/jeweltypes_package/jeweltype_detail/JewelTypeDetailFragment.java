package com.example.catalogos.jeweltypes_package.jeweltype_detail;


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
import com.example.catalogos.jeweltypes_package.add_edit_jeweltype.AddEditJewelTypeActivity;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesActivity;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesFragment;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelType;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

import static com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelType.JEWEL_TYPE_FILE_PATH;

/**
 * Vista para el detalle del Tipo de Joya
 */
public class JewelTypeDetailFragment extends Fragment {
    private static final String ARG_JEWEL_TYPE_ID = "jewelTypeId";

    private String mJewelTypeId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;


    public JewelTypeDetailFragment() {
        // Required empty public constructor
    }

    public static JewelTypeDetailFragment newInstance(String jewelTypeId) {
        JewelTypeDetailFragment fragment = new JewelTypeDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_JEWEL_TYPE_ID, jewelTypeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mJewelTypeId = getArguments().getString(ARG_JEWEL_TYPE_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jeweltype_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadJewelType ();

        return root;
    }

    private void loadJewelType() {
        new JewelTypeDetailFragment.GetJewelTypeByIdTask ().execute();
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
                new JewelTypeDetailFragment.DeleteJewelTypeTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == JewelTypesFragment.REQUEST_UPDATE_DELETE_JEWEL_TYPE) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showJewelType(JewelType jewelType) {
        mCollapsingView.setTitle(jewelType.getName());
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = JEWEL_TYPE_FILE_PATH;
        if (jewelType.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (jewelType.getAvatarUri ()))
                uriStr = jewelType.getAvatarUri ();
            else
                uriStr = filePath + jewelType.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditJewelTypeActivity.class);
        intent.putExtra(JewelTypesActivity.EXTRA_JEWEL_TYPE_ID, mJewelTypeId);
        startActivityForResult(intent, JewelTypesFragment.REQUEST_UPDATE_DELETE_JEWEL_TYPE);
    }

    private void showJewelTypesScreen(boolean requery) {
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
                "Error al eliminar Tipo de Joya", Toast.LENGTH_SHORT).show();
    }

    private class GetJewelTypeByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getJewelTypeById (mJewelTypeId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showJewelType (new JewelType (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteJewelTypeTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteJewelType (mJewelTypeId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showJewelTypesScreen(integer > 0);
        }

    }

}
