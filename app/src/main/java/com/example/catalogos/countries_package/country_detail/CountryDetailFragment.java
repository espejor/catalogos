package com.example.catalogos.countries_package.country_detail;


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
import com.example.catalogos.countries_package.add_edit_country.AddEditCountryActivity;
import com.example.catalogos.countries_package.countries.CountriesActivity;
import com.example.catalogos.countries_package.countries.CountriesFragment;
import com.example.catalogos.countries_package.countries_data.Country;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.example.catalogos.services.ImageSaver;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.apache.commons.io.FilenameUtils;

import java.text.ParseException;

import static com.example.catalogos.countries_package.countries_data.Country.COUNTRY_FILE_PATH;
import static com.example.catalogos.countries_package.countries_data.Country.COUNTRY_FOLDER;
import static com.example.catalogos.countries_package.countries_data.Country.COUNTRY_FILE_PATH;

/**
 * Vista para el detalle del país
 */
public class CountryDetailFragment extends Fragment {
    private static final String ARG_COUNTRY_ID = "countryId";

    private String mCountryId;

    private CollapsingToolbarLayout mCollapsingView;
    private ImageView mAvatar;

    private DbHelper dbHelper;
    private String avatarURI;


    public CountryDetailFragment() {
        // Required empty public constructor
    }

    public static CountryDetailFragment newInstance(String countryId) {
        CountryDetailFragment fragment = new CountryDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_COUNTRY_ID, countryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCountryId = getArguments().getString(ARG_COUNTRY_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_country_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);

        dbHelper = new DbHelper(getActivity());

        loadCountry ();

        return root;
    }

    private void loadCountry() {
        new CountryDetailFragment.GetCountryByIdTask ().execute();
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
                new CountryDetailFragment.DeleteCountryTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CountriesFragment.REQUEST_UPDATE_DELETE_COUNTRY) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showCountry(Country country) {
        mCollapsingView.setTitle(country.getName());
        if (country.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (country.getAvatarUri ()))
                avatarURI = country.getAvatarUri ();
            else
                avatarURI = COUNTRY_FILE_PATH + country.getAvatarUri ();

            Uri uri = Uri.parse (avatarURI);

            mAvatar.setImageURI (uri);
        }
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditCountryActivity.class);
        intent.putExtra(CountriesActivity.EXTRA_COUNTRY_ID, mCountryId);
        startActivityForResult(intent, CountriesFragment.REQUEST_UPDATE_DELETE_COUNTRY);
    }

    private void showCountriesScreen(boolean requery) {
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
                "Error al eliminar País", Toast.LENGTH_SHORT).show();
    }

    private class GetCountryByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getCountryById (mCountryId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showCountry (new Country (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteCountryTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteCountry (mCountryId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(avatarURI != null)
                new ImageSaver (getActivity ()).setDirectory (COUNTRY_FOLDER).setFileName (FilenameUtils.getName (avatarURI)).deleteFile ();

            showCountriesScreen(integer > 0);
        }

    }
}
