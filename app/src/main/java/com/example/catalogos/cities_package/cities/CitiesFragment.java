package com.example.catalogos.cities_package.cities;


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
import com.example.catalogos.cities_package.add_edit_city.AddEditCityActivity;
import com.example.catalogos.cities_package.city_detail.CityDetailActivity;
import com.example.catalogos.database.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;


/**
 * Vista para la lista de subastas del gabinete
 */
public class CitiesFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_CITY = 2;

    private DbHelper dbHelper;

    private ListView mCitiesList;
    private CitiesCursorAdapter mCitiesAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public CitiesFragment() {
        // Required empty public constructor
    }

    public static CitiesFragment newInstance() {
        return new CitiesFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cities, container, false);

        // Referencias UI
        mCitiesList = root.findViewById(R.id.cities_list);
        mCitiesAdapter = new CitiesCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mCitiesList.setAdapter(mCitiesAdapter);

        // Eventos
        mCitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mCitiesAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(CityEntry.ID));

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
        loadCities();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditCityActivity.REQUEST_ADD_CITY:
                    showSuccessfullSavedMessage();
                    loadCities();
                    break;
                case REQUEST_UPDATE_DELETE_CITY:
                    loadCities();
                    break;
            }
        }else{
            loadCities();
        }
    }

    private void loadCities() {
        new CitiesLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "Ciudad guardada correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditCityActivity.class);
        startActivityForResult(intent, AddEditCityActivity.REQUEST_ADD_CITY);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), CityDetailActivity.class);
        intent.putExtra(CitiesActivity.EXTRA_CITY_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_CITY);
    }

    private class CitiesLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllCities ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mCitiesList.setVisibility (View.VISIBLE);
                    mCitiesAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mCitiesList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
