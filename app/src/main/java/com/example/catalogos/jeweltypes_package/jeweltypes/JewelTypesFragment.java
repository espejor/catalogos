package com.example.catalogos.jeweltypes_package.jeweltypes;


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
import com.example.catalogos.jeweltypes_package.add_edit_jeweltype.AddEditJewelTypeActivity;
import com.example.catalogos.jeweltypes_package.jeweltype_detail.JewelTypeDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;


/**
 * Vista para la lista de Tipo de Joyas del gabinete
 */
public class JewelTypesFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_JEWEL_TYPE = 2;

    private DbHelper dbHelper;

    private ListView mJewelTypesList;
    private JewelTypesCursorAdapter mJewelTypesAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public JewelTypesFragment() {
        // Required empty public constructor
    }

    public static com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesFragment newInstance() {
        return new com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_jeweltypes, container, false);

        // Referencias UI
        mJewelTypesList = root.findViewById(R.id.jeweltypes_list);
        mJewelTypesAdapter = new JewelTypesCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mJewelTypesList.setAdapter(mJewelTypesAdapter);

        // Eventos
        mJewelTypesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mJewelTypesAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(JewelTypeEntry.ID));

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
        loadJewelTypes();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditJewelTypeActivity.REQUEST_ADD_JEWEL_TYPE:
                    showSuccessfullSavedMessage();
                    loadJewelTypes();
                    break;
                case REQUEST_UPDATE_DELETE_JEWEL_TYPE:
                    loadJewelTypes();
                    break;
            }
        }else{
            loadJewelTypes();
        }
    }

    private void loadJewelTypes() {
        new JewelTypesLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Tipo de Joya guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditJewelTypeActivity.class);
        startActivityForResult(intent, AddEditJewelTypeActivity.REQUEST_ADD_JEWEL_TYPE);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), JewelTypeDetailActivity.class);
        intent.putExtra(JewelTypesActivity.EXTRA_JEWEL_TYPE_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_JEWEL_TYPE);
    }

    private class JewelTypesLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllJewelTypes ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mJewelTypesList.setVisibility (View.VISIBLE);
                    mJewelTypesAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mJewelTypesList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
