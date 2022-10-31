package com.example.catalogos.designers_package.designer_detail;


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
import com.example.catalogos.designers_package.add_edit_designer.AddEditDesignerActivity;
import com.example.catalogos.designers_package.designers.DesignersActivity;
import com.example.catalogos.designers_package.designers.DesignersFragment;
import com.example.catalogos.designers_package.designers_data.Designer;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

import static com.example.catalogos.designers_package.designers_data.Designer.DESIGNER_FILE_PATH;

/**
 * Vista para el detalle del subasta
 */
public class DesignerDetailFragment extends Fragment {
    private static final String ARG_DESIGNER_ID = "designerId";

    private String mDesignerId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;


    public DesignerDetailFragment() {
        // Required empty public constructor
    }

    public static DesignerDetailFragment newInstance(String designerId) {
        DesignerDetailFragment fragment = new DesignerDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_DESIGNER_ID, designerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDesignerId = getArguments().getString(ARG_DESIGNER_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_designer_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadDesigner ();

        return root;
    }

    private void loadDesigner() {
        new DesignerDetailFragment.GetDesignerByIdTask ().execute();
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
                new DesignerDetailFragment.DeleteDesignerTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DesignersFragment.REQUEST_UPDATE_DELETE_DESIGNER) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showDesigner(Designer designer) {
        mCollapsingView.setTitle(designer.getName());
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = DESIGNER_FILE_PATH;
        if (designer.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (designer.getAvatarUri ()))
                uriStr = designer.getAvatarUri ();
            else
                uriStr = filePath + designer.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        //        Glide.with(this)
//                .load(Uri.parse(filePath + designers.getAvatarUri ()))
//                .centerCrop()
//                .into(mAvatar);
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditDesignerActivity.class);
        intent.putExtra(DesignersActivity.EXTRA_DESIGNER_ID, mDesignerId);
        startActivityForResult(intent, DesignersFragment.REQUEST_UPDATE_DELETE_DESIGNER);
    }

    private void showDesignersScreen(boolean requery) {
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
                "Error al eliminar Diseñador", Toast.LENGTH_SHORT).show();
    }

    private class GetDesignerByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getDesignerById (mDesignerId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showDesigner (new Designer (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteDesignerTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteDesigner (mDesignerId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showDesignersScreen(integer > 0);
        }

    }

}
