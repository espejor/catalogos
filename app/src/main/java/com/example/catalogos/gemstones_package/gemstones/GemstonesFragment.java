package com.example.catalogos.gemstones_package.gemstones;


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
import com.example.catalogos.gemstones_package.add_edit_gemstone.AddEditGemstoneActivity;
import com.example.catalogos.gemstones_package.gemstone_detail.GemstoneDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class GemstonesFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_GEMSTONE = 2;

    private DbHelper dbHelper;

    private ListView mGemstonesList;
    private GemstonesCursorAdapter mGemstonesAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public GemstonesFragment() {
        // Required empty public constructor
    }

    public static GemstonesFragment newInstance() {
        return new GemstonesFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gemstones, container, false);

        // Referencias UI
        mGemstonesList = root.findViewById(R.id.gemstones_list);
        mGemstonesAdapter = new GemstonesCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mGemstonesList.setAdapter(mGemstonesAdapter);

        // Eventos
        mGemstonesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mGemstonesAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(GemstoneEntry.ID));

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
        loadGemstones();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditGemstoneActivity.REQUEST_ADD_GEMSTONE:
                    showSuccessfullSavedMessage();
                    loadGemstones();
                    break;
                case REQUEST_UPDATE_DELETE_GEMSTONE:
                    loadGemstones();
                    break;
            }
        }else{
            loadGemstones();
        }
    }

    private void loadGemstones() {
        new GemstonesLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Piedra Preciosa guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditGemstoneActivity.class);
        startActivityForResult(intent, AddEditGemstoneActivity.REQUEST_ADD_GEMSTONE);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), GemstoneDetailActivity.class);
        intent.putExtra(GemstonesActivity.EXTRA_GEMSTONE_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_GEMSTONE);
    }

    private class GemstonesLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllGemstones ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mGemstonesList.setVisibility (View.VISIBLE);
                    mGemstonesAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mGemstonesList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
