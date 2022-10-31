package com.example.catalogos.periods_package.add_edit_period;


import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.catalogos.R;
import com.example.catalogos.database.DbHelper;
import com.example.catalogos.periods_package.periods_data.Period;
import com.example.catalogos.services.DataConvert;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static android.app.Activity.RESULT_OK;

/**
 * Vista para creación/edición de un subasta
 */
public class AddEditPeriodFragment extends Fragment {
    private static final String ARG_PERIOD_ID = "arg_period_id";
    private static final int REQUEST_SEARCH_PICTURE_CODE = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;

    private String mPeriodId;

    private DbHelper dbHelper;

    private FloatingActionButton mSaveButton;
    private TextInputEditText mNameField;
    private TextInputLayout mNameLabel;
    private TextInputEditText mInitField;
    private TextInputLayout mInitLabel;
    private TextInputEditText mFinalField;
    private TextInputLayout mFinalLabel;


    public AddEditPeriodFragment() {
        // Required empty public constructor
    }

    public static AddEditPeriodFragment newInstance(String periodId) {
        AddEditPeriodFragment fragment = new AddEditPeriodFragment ();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_edit_period, container, false);

        // Referencias UI
        mSaveButton = getActivity().findViewById(R.id.fab);
        mNameLabel = root.findViewById(R.id.til_name);
        mNameField = root.findViewById(R.id.et_name);
        mInitLabel = root.findViewById(R.id.til_init);
        mInitField = root.findViewById(R.id.et_init);
        mFinalLabel = root.findViewById(R.id.til_final);
        mFinalField = root.findViewById(R.id.et_final);


        // Eventos
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditPeriod ();
            }
        });

        dbHelper = new DbHelper(getActivity());

        // Carga de datos
        if (mPeriodId != null) {
            loadPeriod ();
        }

        return root;
    }

    private void loadPeriod() {
        new GetPeriodByIdTask ().execute();
    }

    private void addEditPeriod() {
        boolean error = false;

        String name = mNameField.getText ().toString ();
        String initYear = mInitField.getText ().toString ();
        String finalYear = mFinalField.getText ().toString ();

        if (TextUtils.isEmpty(name)) {
            mNameLabel.setError(getString(R.string.field_error));
            error = true;
        }

        if (error) {
            return;
        }

        Period period = new Period (name,Integer.parseInt (initYear),Integer.parseInt (finalYear));

        new AddEditPeriodTask ().execute(period);

    }

    private void showAuctionsScreen(Boolean requery) {
        if (!requery) {
            showAddEditError();
            getActivity().setResult(Activity.RESULT_CANCELED);
        } else {
            getActivity().setResult(RESULT_OK);
        }

        getActivity().finish();
    }

    private void showAddEditError() {
        Toast.makeText(getActivity(),
                "Error al agregar nueva información", Toast.LENGTH_SHORT).show();
    }

    private void showPeriod(Period period) {
        mNameField.setText(period.getName());
        mInitField.setText(String.valueOf (period.getInitYear ()));
        mFinalField.setText(String.valueOf (period.getFinalYear ()));
    }


    private void showLoadError() {
        Toast.makeText(getActivity(),
                "Error al editar Periodo", Toast.LENGTH_SHORT).show();
    }

    private class GetPeriodByIdTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.getPeriodById(mPeriodId);
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
                getActivity().setResult(Activity.RESULT_CANCELED);
                getActivity().finish();
            }
        }

    }

    private class AddEditPeriodTask extends AsyncTask<Period, Void, Boolean> {
        Period period;

        @Override
        protected Boolean doInBackground(Period... periods) {
            this.period = periods[0];
            if (mPeriodId != null) {
                return dbHelper.updatePeriod(periods[0], mPeriodId) > 0;
            } else {
                return dbHelper.savePeriod(periods[0]) > 0;
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
//            if(result)
//                if(!new DataConvert().isUriFromContent (strURLAvatar))  // No es una URI de la Galería
//                    new SavePictureFromURI (strURLAvatar, period.getName (),"images/periods").execute ();

            showAuctionsScreen(result);
        }

    }

}
