package com.example.catalogos.countries_package.countries;


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
import com.example.catalogos.countries_package.add_edit_country.AddEditCountryActivity;
import com.example.catalogos.countries_package.country_detail.CountryDetailActivity;
import com.example.catalogos.database.DbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.catalogos.countries_package.countries_data.CountriesContract.CountryEntry;


/**
 * Vista para la lista de países del gabinete
 */
public class CountriesFragment extends Fragment {
    public static final int REQUEST_UPDATE_DELETE_COUNTRY = 2;

    private DbHelper dbHelper;

    private ListView mCountriesList;
    private CountriesCursorAdapter mCountriesAdapter;
    private FloatingActionButton mAddButton;
    private TextView mTextEmptyList;


    public CountriesFragment() {
        // Required empty public constructor
    }

    public static CountriesFragment newInstance() {
        return new CountriesFragment ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_countries, container, false);

        // Referencias UI
        mCountriesList = root.findViewById(R.id.countries_list);
        mCountriesAdapter = new CountriesCursorAdapter (getActivity(), null);
        mAddButton =  getActivity().findViewById(R.id.fab);
        mTextEmptyList = root.findViewById (R.id.text_empty_list);

        // Setup
        mCountriesList.setAdapter(mCountriesAdapter);

        // Eventos
        mCountriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mCountriesAdapter.getItem(i);
                String currentAuctionId = currentItem.getString(
                        currentItem.getColumnIndex(CountryEntry.ID));

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
        loadCountries();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddEditCountryActivity.REQUEST_ADD_COUNTRY:
                    showSuccessfullSavedMessage();
                    loadCountries();
                    break;
                case REQUEST_UPDATE_DELETE_COUNTRY:
                    loadCountries();
                    break;
            }
        }else{
            loadCountries();
        }
    }

    private void loadCountries() {
        new CountriesLoadTask().execute();
    }

    private void showSuccessfullSavedMessage() {
        Toast.makeText(getActivity(),
                "País guardado correctamente", Toast.LENGTH_SHORT).show();
    }

    private void showAddScreen() {
        Intent intent = new Intent(getActivity(), AddEditCountryActivity.class);
        startActivityForResult(intent, AddEditCountryActivity.REQUEST_ADD_COUNTRY);
    }

    private void showDetailScreen(String auctionId) {
        Intent intent = new Intent(getActivity(), CountryDetailActivity.class);
        intent.putExtra(CountriesActivity.EXTRA_COUNTRY_ID, auctionId);
        startActivityForResult(intent, REQUEST_UPDATE_DELETE_COUNTRY);
    }

    private class CountriesLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getAllCountries ();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null)
                cursor.moveToFirst ();
                if(cursor.getCount() > 0) {
                    mTextEmptyList.setVisibility (View.INVISIBLE);
                    mCountriesList.setVisibility (View.VISIBLE);
                    mCountriesAdapter.swapCursor(cursor);
            } else {
                // Mostrar empty state
                    mTextEmptyList.setVisibility (View.VISIBLE);
                    mCountriesList.setVisibility (View.INVISIBLE);
            }
        }
    }

}
