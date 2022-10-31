package com.example.catalogos.owners_package.owners;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.owners_package.add_edit_owner.AddEditOwnerActivity;
import com.example.catalogos.owners_package.owner_detail.OwnerDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class OwnersFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_OWNER = 2;

    private DbHelper dbHelper;

    private ListView mOwnersList;
    private OwnersCursorAdapter mOwnersAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public OwnersFragment() {
        // Required empty public constructor
    }

    public static OwnersFragment newInstance() {
        return new OwnersFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_owners, container, false);

        // Referencias UI
        mOwnersList = root.findViewById(R.id.owners_list);
        mOwnersAdapter = new OwnersCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mOwnersList.setAdapter(mOwnersAdapter);

        // Eventos
        mOwnersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mOwnersAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(OwnerEntry.ID));

                showDetailScreen(currentAuctionId);
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScreen();
            }
        });


//        getActivity().deleteDatabase(DbHelper.DATABASE_NAME);

        // Instancia de helper
        dbHelper = new DbHelper(getActivity());

        // Carga de datos
        loadOwners();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditOwnerActivity.REQUEST_ADD_OWNER:
                    showSuccessfullSavedMessage();
                    loadOwners();
                    break;
                case REQUEST_UPDATE_DELETE_OWNER:
                    loadOwners();
                    break;
            }
        }else{
            loadOwners();
        }
    }

    private void loadOwners() {
        new OwnersLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Propietario guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditOwnerActivity.class);
        startActivityForResult(intent, AddEditOwnerActivity.REQUEST_ADD_OWNER);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), OwnerDetailActivity.class);
        intent.putExtra(OwnersActivity.EXTRA_OWNER_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_OWNER);
    }

    private class OwnersLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllOwners ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mOwnersList.setVisibility (View.VISIBLE);
                    mOwnersAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mOwnersList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
