package com.example.catalogos.cuts_package.cuts;


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
import com.example.catalogos.cuts_package.add_edit_cut.AddEditCutActivity;
import com.example.catalogos.cuts_package.cut_detail.CutDetailActivity;
import com.example.catalogos.database.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class CutsFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_CUT = 2;

    private DbHelper dbHelper;

    private ListView mCutsList;
    private CutsCursorAdapter mCutsAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public CutsFragment() {
        // Required empty public constructor
    }

    public static CutsFragment newInstance() {
        return new CutsFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cuts, container, false);

        // Referencias UI
        mCutsList = root.findViewById(R.id.cuts_list);
        mCutsAdapter = new CutsCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mCutsList.setAdapter(mCutsAdapter);

        // Eventos
        mCutsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mCutsAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(CutEntry.ID));

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
        loadCuts();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditCutActivity.REQUEST_ADD_CUT:
                    showSuccessfullSavedMessage();
                    loadCuts();
                    break;
                case REQUEST_UPDATE_DELETE_CUT:
                    loadCuts();
                    break;
            }
        }else{
            loadCuts();
        }
    }

    private void loadCuts() {
        new CutsLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Tipo de Talla guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditCutActivity.class);
        startActivityForResult(intent, AddEditCutActivity.REQUEST_ADD_CUT);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), CutDetailActivity.class);
        intent.putExtra(CutsActivity.EXTRA_CUT_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_CUT);
    }

    private class CutsLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllCuts ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mCutsList.setVisibility (View.VISIBLE);
                    mCutsAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mCutsList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
