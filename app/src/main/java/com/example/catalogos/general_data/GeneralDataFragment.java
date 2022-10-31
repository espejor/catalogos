package com.example.catalogos.general_data;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseActivity;
import com.example.catalogos.cities_package.cities.CitiesActivity;
import com.example.catalogos.countries_package.countries.CountriesActivity;
import com.example.catalogos.cuts_package.cuts.CutsActivity;
import com.example.catalogos.designers_package.designers.DesignersActivity;
import com.example.catalogos.gemstones_package.gemstones.GemstonesActivity;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesActivity;
import com.example.catalogos.owners_package.owners.OwnersActivity;
import com.example.catalogos.periods_package.periods.PeriodsActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GeneralDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GeneralDataFragment extends Fragment {

    private CardView
            cvAuctionsHouses, 
            cvCities, 
            cvCountries, 
            cvJewelsTypes, 
            cvDesigners,
            cvCuts, 
            cvOwners, 
            cvGemstones, 
            cvPeriods;

    public GeneralDataFragment(){
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GeneralDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GeneralDataFragment newInstance(){
        GeneralDataFragment fragment = new GeneralDataFragment ();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate (savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View root = inflater.inflate (R.layout.fragment_general_data, container, false);

        // Referencias UI
        cvAuctionsHouses = root.findViewById (R.id.btn_auctions_houses);
        cvCities         = root.findViewById (R.id.btn_cities);
        cvCountries      = root.findViewById (R.id.btn_countries);
        cvJewelsTypes    = root.findViewById (R.id.btn_jewels_types);
        cvDesigners      = root.findViewById (R.id.btn_designers);
        cvCuts           = root.findViewById (R.id.btn_cuts);
        cvOwners         = root.findViewById (R.id.btn_owners);
        cvGemstones      = root.findViewById (R.id.btn_gemstones);
        cvPeriods        = root.findViewById (R.id.btn_periods);

        // Eventos

        cvAuctionsHouses.setOnClickListener(auctionsHousesListener);
        cvCities        .setOnClickListener(cvCitiesListener);
        cvCountries     .setOnClickListener(cvCountriesListener);
        cvJewelsTypes   .setOnClickListener(cvJewelsTypesListener);
        cvDesigners     .setOnClickListener(cvDesignersListener);
        cvCuts          .setOnClickListener(cvCutsListener);
        cvOwners        .setOnClickListener(cvOwnersListener);
        cvGemstones     .setOnClickListener(cvGemstonesListener);
        cvPeriods       .setOnClickListener(cvPeriodsListener);

        return root;
    }

    View.OnClickListener auctionsHousesListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), AuctionsHouseActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvCitiesListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), CitiesActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvCountriesListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), CountriesActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvJewelsTypesListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), JewelTypesActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvDesignersListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), DesignersActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvCutsListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), CutsActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvOwnersListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), OwnersActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvGemstonesListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), GemstonesActivity.class);
            startActivity(intent);
        }
    };

    View.OnClickListener cvPeriodsListener = new View.OnClickListener (){

        @Override
        public void onClick(View v){
            Intent intent = new Intent (getContext (), PeriodsActivity.class);
            startActivity(intent);
        }
    };
}