package com.example.catalogos.cities_package.city_detail;


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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.cities_package.add_edit_city.AddEditCityActivity;
import com.example.catalogos.cities_package.cities.CitiesActivity;
import com.example.catalogos.cities_package.cities.CitiesFragment;
import com.example.catalogos.cities_package.cities_data.City;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.example.catalogos.services.ImageSaver;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import org.apache.commons.io.FilenameUtils;

import java.text.ParseException;

import static com.example.catalogos.cities_package.cities_data.City.CITY_FILE_PATH;
import static com.example.catalogos.cities_package.cities_data.City.CITY_FOLDER;

/**
 * Vista para el detalle del subasta
 */
public class CityDetailFragment extends Fragment implements ActivityResultCaller {
    private static final String ARG_CITY_ID = "cityId";

    private String mCityId;

    private CollapsingToolbarLayout mCollapsingView;

    private TextView mCountry;
    private ImageView mAvatar;

    private DbHelper dbHelper;
    private String avatarURI;


    public CityDetailFragment() {
        // Required empty public constructor
    }

    public static CityDetailFragment newInstance(String cityId) {
        CityDetailFragment fragment = new CityDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_CITY_ID, cityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mCityId = getArguments().getString(ARG_CITY_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_city_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mAvatar = getActivity().findViewById(R.id.iv_avatar);
        mCountry = root.findViewById(R.id.et_country);

        dbHelper = new DbHelper(getActivity());

        loadCity ();

        return root;
    }

    private void loadCity() {
        new GetCityByIdTask ().execute();
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
                new DeleteCityTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CitiesFragment.REQUEST_UPDATE_DELETE_CITY) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showCity(City city) {
        mCollapsingView.setTitle(city.getName());
        if (city.getAvatarUri () == null) {
            mAvatar.setImageResource (R.drawable.ic_baseline_error_24);
        }else {
            if (new DataConvert ().isUriFromMemory (city.getAvatarUri ()))
                avatarURI = city.getAvatarUri ();
            else
                avatarURI = CITY_FILE_PATH + city.getAvatarUri ();

            Uri uri = Uri.parse (avatarURI);

            mAvatar.setImageURI (uri);
        }
        mCountry.setText(city.getCountry ());
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditCityActivity.class);
        intent.putExtra(CitiesActivity.EXTRA_CITY_ID, mCityId);
        startActivityForResult(intent, CitiesFragment.REQUEST_UPDATE_DELETE_CITY);
    }

    private void showCitiesScreen(boolean requery) {
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
                "Error al eliminar Ciudad", Toast.LENGTH_SHORT).show();
    }

    private class GetCityByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getViewCityById (mCityId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showCity (new City (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeleteCityTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deleteCity (mCityId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(avatarURI != null)
                new ImageSaver (getActivity ()).setDirectory (CITY_FOLDER).setFileName (FilenameUtils.getName (avatarURI)).deleteFile ();

            showCitiesScreen(integer > 0);
        }

    }

}
