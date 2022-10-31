package com.example.catalogos.periods_package.add_edit_period;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.catalogos.R;
import com.example.catalogos.periods_package.periods.PeriodsActivity;

public class AddEditPeriodActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_PERIOD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_period);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String periodId = getIntent().getStringExtra(PeriodsActivity.EXTRA_PERIOD_ID);

        setTitle(periodId == null ? "Añadir Periodo" : "Editar Periodo");

        AddEditPeriodFragment addEditPeriodFragment = (AddEditPeriodFragment)
                getSupportFragmentManager().findFragmentById(R.id.add_edit_period_container);
        if (addEditPeriodFragment == null) {
            addEditPeriodFragment = AddEditPeriodFragment.newInstance(periodId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.add_edit_period_container, addEditPeriodFragment)
                    .commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
