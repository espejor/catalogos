package com.example.catalogos.periods_package.period_detail;


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

import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.periods_package.add_edit_period.AddEditPeriodActivity;
import com.example.catalogos.periods_package.periods.PeriodsActivity;
import com.example.catalogos.periods_package.periods.PeriodsFragment;
import com.example.catalogos.periods_package.periods_data.Period;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.GoHome;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.text.ParseException;

import static com.example.catalogos.periods_package.periods_data.Period.PERIOD_FILE_PATH;

/**
 * Vista para el detalle del subasta
 */
public class PeriodDetailFragment extends Fragment {
    private static final String ARG_PERIOD_ID = "periodId";

    private String mPeriodId;

    private CollapsingToolbarLayout mCollapsingView;
    private TextView mInitTextView,mFinalTextView;



    private DbHelper dbHelper;


    public PeriodDetailFragment() {
        // Required empty public constructor
    }

    public static PeriodDetailFragment newInstance(String periodId) {
        PeriodDetailFragment fragment = new PeriodDetailFragment ();
        Bundle args = new Bundle();
        args.putString(ARG_PERIOD_ID, periodId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPeriodId = getArguments().getString(ARG_PERIOD_ID);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_period_detail, container, false);
        mCollapsingView = getActivity().findViewById(R.id.toolbar_layout);
        mInitTextView = root.findViewById (R.id.tv_init);
        mFinalTextView = root.findViewById (R.id.tv_final);

        dbHelper = new DbHelper(getActivity());

        loadPeriod ();

        return root;
    }

    private void loadPeriod() {
        new GetPeriodByIdTask ().execute();
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
                new DeletePeriodTask ().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PeriodsFragment.REQUEST_UPDATE_DELETE_PERIOD) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        }
    }

    private void showPeriod(Period period) {
        mCollapsingView.setTitle(period.getName());
        mInitTextView.setText (String.valueOf (period.getInitYear ()));
        mFinalTextView.setText (String.valueOf (period.getFinalYear ()));
    }

    private void showEditScreen() {
        Intent intent = new Intent(getActivity(), AddEditPeriodActivity.class);
        intent.putExtra(PeriodsActivity.EXTRA_PERIOD_ID, mPeriodId);
        startActivityForResult(intent, PeriodsFragment.REQUEST_UPDATE_DELETE_PERIOD);
    }

    private void showPeriodsScreen(boolean requery) {
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
                "Error al eliminar Periodo", Toast.LENGTH_SHORT).show();
    }

    private class GetPeriodByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getPeriodById (mPeriodId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.moveToLast()) {
                try {
                    showPeriod (new Period (cursor));
                } catch (ParseException e) {
                    e.printStackTrace ();
                }
            } else {
                showLoadError();
            }
        }

    }

    private class DeletePeriodTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return dbHelper.deletePeriod (mPeriodId);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            showPeriodsScreen(integer > 0);
        }

    }

}
