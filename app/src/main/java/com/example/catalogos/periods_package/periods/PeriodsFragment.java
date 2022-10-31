package com.example.catalogos.periods_package.periods;


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
import com.example.catalogos.periods_package.add_edit_period.AddEditPeriodActivity;
import com.example.catalogos.periods_package.period_detail.PeriodDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class PeriodsFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_PERIOD = 2;

    private DbHelper dbHelper;

    private ListView mPeriodsList;
    private PeriodsCursorAdapter mPeriodsAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public PeriodsFragment() {
        // Required empty public constructor
    }

    public static PeriodsFragment newInstance() {
        return new PeriodsFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_periods, container, false);

        // Referencias UI
        mPeriodsList = root.findViewById(R.id.periods_list);
        mPeriodsAdapter = new PeriodsCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mPeriodsList.setAdapter(mPeriodsAdapter);

        // Eventos
        mPeriodsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mPeriodsAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(PeriodEntry.ID));

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
        loadPeriods();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditPeriodActivity.REQUEST_ADD_PERIOD:
                    showSuccessfullSavedMessage();
                    loadPeriods();
                    break;
                case REQUEST_UPDATE_DELETE_PERIOD:
                    loadPeriods();
                    break;
            }
        }else{
            loadPeriods();
        }
    }

    private void loadPeriods() {
        new PeriodsLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Periodo guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditPeriodActivity.class);
        startActivityForResult(intent, AddEditPeriodActivity.REQUEST_ADD_PERIOD);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), PeriodDetailActivity.class);
        intent.putExtra(PeriodsActivity.EXTRA_PERIOD_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_PERIOD);
    }

    private class PeriodsLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllPeriods ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mPeriodsList.setVisibility (View.VISIBLE);
                    mPeriodsAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mPeriodsList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
