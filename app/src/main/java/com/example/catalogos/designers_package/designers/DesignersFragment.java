package com.example.catalogos.designers_package.designers;


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
import com.example.catalogos.designers_package.add_edit_designer.AddEditDesignerActivity;
import com.example.catalogos.designers_package.designer_detail.DesignerDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class DesignersFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_DESIGNER = 2;

    private DbHelper dbHelper;

    private ListView mDesignersList;
    private DesignersCursorAdapter mDesignersAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public DesignersFragment() {
        // Required empty public constructor
    }

    public static DesignersFragment newInstance() {
        return new DesignersFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_designers, container, false);

        // Referencias UI
        mDesignersList = root.findViewById(R.id.designers_list);
        mDesignersAdapter = new DesignersCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mDesignersList.setAdapter(mDesignersAdapter);

        // Eventos
        mDesignersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mDesignersAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(DesignerEntry.ID));

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
        loadDesigners();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditDesignerActivity.REQUEST_ADD_DESIGNER:
                    showSuccessfullSavedMessage();
                    loadDesigners();
                    break;
                case REQUEST_UPDATE_DELETE_DESIGNER:
                    loadDesigners();
                    break;
            }
        }else{
            loadDesigners();
        }
    }

    private void loadDesigners() {
        new DesignersLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Diseñador guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditDesignerActivity.class);
        startActivityForResult(intent, AddEditDesignerActivity.REQUEST_ADD_DESIGNER);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), DesignerDetailActivity.class);
        intent.putExtra(DesignersActivity.EXTRA_DESIGNER_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_DESIGNER);
    }

    private class DesignersLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllDesigners ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mDesignersList.setVisibility (View.VISIBLE);
                    mDesignersAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mDesignersList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
