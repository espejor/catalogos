package com.example.catalogos.hallmarks_package.hallmarks;


import static com.example.catalogos.hallmarks_package.hallmarks_data.HallmarksContract.HallmarkEntry;

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
import com.example.catalogos.hallmarks_package.add_edit_hallmark.AddEditHallmarkActivity;
import com.example.catalogos.hallmarks_package.hallmark_detail.HallmarkDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * Vista para la lista de subastas del gabinete
 */
public class HallmarksFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_HALLMARK = 2;

    private DbHelper dbHelper;

    private ListView mHallmarksList;
    private HallmarksCursorAdapter mHallmarksAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public HallmarksFragment() {
        // Required empty public constructor
    }

    public static HallmarksFragment newInstance() {
        return new HallmarksFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hallmarks, container, false);

        // Referencias UI
        mHallmarksList = root.findViewById(R.id.hallmarks_list);
        mHallmarksAdapter = new HallmarksCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mHallmarksList.setAdapter(mHallmarksAdapter);

        // Eventos
        mHallmarksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mHallmarksAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(HallmarkEntry.ID));

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
        loadHallmarks();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditHallmarkActivity.REQUEST_ADD_HALLMARK:
                    showSuccessfullSavedMessage();
                    loadHallmarks();
                    break;
                case REQUEST_UPDATE_DELETE_HALLMARK:
                    loadHallmarks();
                    break;
            }
        }else{
            loadHallmarks();
        }
    }

    private void loadHallmarks() {
        new HallmarksLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Propietario guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditHallmarkActivity.class);
        startActivityForResult(intent, AddEditHallmarkActivity.REQUEST_ADD_HALLMARK);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), HallmarkDetailActivity.class);
        intent.putExtra(HallmarksActivity.EXTRA_HALLMARK_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_HALLMARK);
    }

    private class HallmarksLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllHallmarks ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null) {
                cursor.moveToFirst ();
                if (cursor.getCount () > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mHallmarksList.setVisibility (View.VISIBLE);
                    mHallmarksAdapter.swapCursor (cursor);
                } else {
                    // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mHallmarksList.setVisibility (View.INVISIBLE);
                }
            }
        }
    }

}
