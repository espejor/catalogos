package com.example.catalogos.owners_package.owner_detail;


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
import com.example.catalogos.owners_package.add_edit_owner.AddEditOwnerActivity;
import com.example.catalogos.owners_package.owner_detail.OwnerDetailFragment;
import com.example.catalogos.owners_package.owners.OwnersActivity;
import com.example.catalogos.owners_package.owners.OwnersFragment;
import com.example.catalogos.owners_package.owners_data.Owner;
import com.example.catalogos.owners_package.add_edit_owner.AddEditOwnerActivity;
import com.example.catalogos.owners_package.owners.OwnersActivity;
import com.example.catalogos.owners_package.owners.OwnersFragment;
import com.example.catalogos.owners_package.owners_data.Owner;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

import static com.example.catalogos.owners_package.owners_data.Owner.OWNER_FILE_PATH;
import static com.example.catalogos.owners_package.owners_data.Owner.OWNER_FILE_PATH;

/**
 * Vista para el detalle del subasta
 */
public class OwnerDetailFragment extends Fragment {
    private static final String ARG_OWNER_ID = "ownerId";

    private String mOwnerId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;


    public OwnerDetailFragment() {
        // Required empty public constructor
    }

    public static OwnerDetailFragment newInstance(String ownerId) {
        OwnerDetailFragment fragment = new OwnerDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_OWNER_ID, ownerId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mOwnerId = getArguments().getString(ARG_OWNER_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_owner_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadOwner ();

        return root;
    }

    private void loadOwner() {
        new OwnerDetailFragment.GetOwnerByIdTask ().execute();
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
                new OwnerDetailFragment.DeleteOwnerTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OwnersFragment.REQUEST_UPDATE_DELETE_OWNER) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showOwner(Owner owner) {
        mCollapsingView.setTitle(owner.getName());
//        String folderPath = App.getContext ().getFilesDir().getAbsolutePath();
        String filePath = OWNER_FILE_PATH;
        if (owner.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            String uriStr;
            if (new DataConvert ().isUriFromMemory (owner.getAvatarUri ()))
                uriStr = owner.getAvatarUri ();
            else
                uriStr = filePath + owner.getAvatarUri ();

            Uri uri = Uri.parse (uriStr);

            mAvatar.setImageURI (uri);
        }
        //        Glide.with(this)
//                .load(Uri.parse(filePath + owners.getAvatarUri ()))
//                .centerCrop()
//                .into(mAvatar);
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditOwnerActivity.class);
        intent.putExtra(OwnersActivity.EXTRA_OWNER_ID, mOwnerId);
        startActivityForResult(intent, OwnersFragment.REQUEST_UPDATE_DELETE_OWNER);
    }

    private void showOwnersScreen(boolean requery) {
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

    private class GetOwnerByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getOwnerById (mOwnerId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showOwner (new Owner (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteOwnerTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteOwner (mOwnerId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showOwnersScreen(integer > 0);
        }

    }

}
