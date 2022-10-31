package com.example.catalogos.jewels_package.jewel_search;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.catalogos.R;
import com.example.catalogos.auctions_house_package.auctions_house.AuctionsHouseCursorAdapter;
import com.example.catalogos.auctions_house_package.auctions_house_data.AuctionsHouseContract.AuctionHouseEntry;
import com.example.catalogos.auctions_package.auctions.AuctionsFragment;
import com.example.catalogos.cities_package.cities.CitiesCursorAdapter;
import com.example.catalogos.cities_package.cities_data.CitiesContract.CityEntry;
import com.example.catalogos.countries_package.countries.CountriesCursorAdapter;
import com.example.catalogos.countries_package.countries_data.CountriesContract;
import com.example.catalogos.countries_package.countries_data.CountriesContract.CountryEntry;
import com.example.catalogos.cuts_package.cuts.CutsCursorAdapter;
import com.example.catalogos.cuts_package.cuts_data.CutsContract;
import com.example.catalogos.cuts_package.cuts_data.CutsContract.CutEntry;
import com.example.catalogos.designers_package.designers.DesignersCursorAdapter;
import com.example.catalogos.designers_package.designers_data.DesignersContract;
import com.example.catalogos.designers_package.designers_data.DesignersContract.DesignerEntry;
import com.example.catalogos.dialog_package.SearchingDialog;
import com.example.catalogos.gemstones_package.gemstones.GemstonesCursorAdapter;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract;
import com.example.catalogos.gemstones_package.gemstones_data.GemstonesContract.GemstoneEntry;
import com.example.catalogos.jewels_package.jewels.JewelsByAuctionActivity;
import com.example.catalogos.jeweltypes_package.jeweltypes.JewelTypesCursorAdapter;
import com.example.catalogos.jeweltypes_package.jeweltypes_data.JewelTypesContract.JewelTypeEntry;
import com.example.catalogos.owners_package.owners.OwnersCursorAdapter;
import com.example.catalogos.owners_package.owners_data.OwnersContract;
import com.example.catalogos.owners_package.owners_data.OwnersContract.OwnerEntry;
import com.example.catalogos.pdf_creator.PDFCreator;
import com.example.catalogos.periods_package.periods.PeriodsCursorAdapter;
import com.example.catalogos.periods_package.periods_data.PeriodsContract;
import com.example.catalogos.periods_package.periods_data.PeriodsContract.PeriodEntry;
import com.example.catalogos.services.DataConvert;
import com.example.catalogos.services.DateInputMask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.util.Date;

import static com.example.catalogos.auctions_package.auctions.AuctionsActivity.EXTRA_BUNDLE_SEARCH;
import static com.example.catalogos.services.DataConvert.formatSP;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JewelSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JewelSearchFragment extends Fragment {
    public static final String BUNDLE_DATA_JEWEL_TYPE = "jewelType";
    public static final String BUNDLE_DATA_AUCTION_HOUSE = "auctionHouse";
    public static final String BUNDLE_DATA_CITY = "city";
    public static final String BUNDLE_DATA_COUNTRY = "country";
    public static final String BUNDLE_DATA_DESIGNER = "designer";
    public static final String BUNDLE_DATA_OWNER = "owner";
    public static final String BUNDLE_DATA_GEMSTONE = "gemstone";
    public static final String BUNDLE_DATA_CUT = "cut";
    public static final String BUNDLE_DATA_PERIOD = "period";
    public static final String BUNDLE_DATA_OBS = "obs";
    public static final String BUNDLE_DATA_DATE_FROM = "date_from";
    public static final String BUNDLE_DATA_DATE_TO = "date_to";

    private TextInputEditText mJewelTypeField;
    private TextInputEditText mAuctionHouseField;
    private TextInputEditText mCityField;
    private TextInputEditText mCountryField;
    private TextInputEditText mDesignerField;
    private TextInputEditText mOwnerField;
    private TextInputEditText mGemstoneField;
    private TextInputEditText mCutField;
    private TextInputEditText mPeriodField;
    private TextInputEditText mObsField;
    private TextInputEditText mDateFromField;
    private TextInputEditText mDateToField;


    private CursorAdapter mJewelTypeAdapter;
    private CursorAdapter mAuctionHouseAdapter;
    private CursorAdapter mCityAdapter;
    private CursorAdapter mCountryAdapter;
    private CursorAdapter mDesignerAdapter;
    private CursorAdapter mOwnerAdapter;
    private CursorAdapter mGemstoneAdapter;
    private CursorAdapter mCutAdapter;
    private CursorAdapter mPeriodAdapter;

    private FloatingActionButton searchButton;
    private TextView mCleanFormText;

    public JewelSearchFragment(){
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static JewelSearchFragment newInstance(){
        JewelSearchFragment fragment = new JewelSearchFragment ();
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
        View root = inflater.inflate (R.layout.fragment_jewel_search, container, false);

        mJewelTypeAdapter = new JewelTypesCursorAdapter (getActivity(), null);
        mAuctionHouseAdapter = new AuctionsHouseCursorAdapter (getActivity(), null);
        mCityAdapter = new CitiesCursorAdapter (getActivity(), null);
        mCountryAdapter = new CountriesCursorAdapter (getActivity(), null);
        mDesignerAdapter = new DesignersCursorAdapter (getActivity(), null);
        mOwnerAdapter = new OwnersCursorAdapter (getActivity(), null);
        mGemstoneAdapter = new GemstonesCursorAdapter (getActivity(), null);
        mCutAdapter = new CutsCursorAdapter (getActivity(), null);
        mPeriodAdapter = new PeriodsCursorAdapter (getActivity(), null);

        mJewelTypeField = root.findViewById(R.id.tiet_jewel_type);
        mAuctionHouseField = root.findViewById(R.id.tiet_auction_house);
        mCityField = root.findViewById(R.id.tiet_city);
        mCountryField = root.findViewById(R.id.tiet_country);
        mDesignerField = root.findViewById(R.id.tiet_designer);
        mOwnerField = root.findViewById(R.id.tiet_owner);
        mGemstoneField = root.findViewById(R.id.tiet_gemstone);
        mCutField = root.findViewById(R.id.tiet_cut);
        mPeriodField = root.findViewById(R.id.tiet_period);
        mObsField = root.findViewById(R.id.tiet_obs);
        mDateFromField = root.findViewById(R.id.tiet_date_from);
        mDateToField = root.findViewById(R.id.tiet_date_to);

        new DateInputMask (mDateFromField);
        new DateInputMask (mDateToField);

        mJewelTypeField.setCursorVisible (false);
        mAuctionHouseField.setCursorVisible (false);
        mCityField.setCursorVisible (false);
        mCountryField.setCursorVisible (false);
        mDesignerField.setCursorVisible (false);
        mOwnerField.setCursorVisible (false);
        mGemstoneField.setCursorVisible (false);
        mCutField.setCursorVisible (false);
        mPeriodField.setCursorVisible (false);


        mCleanFormText = root.findViewById (R.id.text_clean_form);


        searchButton = root.findViewById(R.id.search_btn);

        mJewelTypeField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mJewelTypeAdapter, JewelTypeEntry.TABLE_NAME, JewelTypeEntry.NAME,R.string.searching_jewel_type,mJewelTypeField );
            }
        });

        mAuctionHouseField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mAuctionHouseAdapter, AuctionHouseEntry.TABLE_NAME, AuctionHouseEntry.NAME,R.string.searching_auction_house,mAuctionHouseField );
            }
        });

        mCityField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mCityAdapter, CityEntry.TABLE_NAME, CityEntry.NAME,R.string.searching_city,mCityField );
            }
        });

        mCountryField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mCountryAdapter, CountryEntry.TABLE_NAME, CountryEntry.NAME,R.string.searching_country,mCountryField );
            }
        });

        mDesignerField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mDesignerAdapter, DesignerEntry.TABLE_NAME, DesignerEntry.NAME,R.string.searching_designer,mDesignerField );
            }
        });

        mOwnerField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mOwnerAdapter, OwnerEntry.TABLE_NAME, OwnerEntry.NAME,R.string.searching_owners,mOwnerField );
            }
        });

        mGemstoneField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mGemstoneAdapter, GemstoneEntry.TABLE_NAME, GemstoneEntry.NAME,R.string.searching_gemstones,mGemstoneField );
            }
        });

        mCutField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mCutAdapter, CutEntry.TABLE_NAME, CutEntry.NAME,R.string.searching_cuts,mCutField );
            }
        });

        mPeriodField.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                onClickListener(mPeriodAdapter, PeriodEntry.TABLE_NAME, PeriodEntry.NAME,R.string.searching_period,mPeriodField );
            }
        });

        mDateFromField.setOnFocusChangeListener (new View.OnFocusChangeListener () {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if (mDateFromField.getText ().toString ().equals ("DD-MM-YYYY"))
                    mDateFromField.setText ("");
            }
        });

        mCleanFormText.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                mJewelTypeField.setText ("");
                mAuctionHouseField.setText ("");
                mCityField.setText ("");
                mCountryField.setText ("");
                mDesignerField.setText ("");
                mOwnerField.setText ("");
                mGemstoneField.setText ("");
                mCutField.setText ("");
                mPeriodField.setText ("");
                mObsField.setText ("");
                mDateFromField.setText ("");
                mDateToField.setText ("");
            }
        });

        searchButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                searchJewel();
            }
        });
        return root;
    }

    private void searchJewel(){
        String jewelType = mJewelTypeField.getText ().toString ();
        String auctionHouse = mAuctionHouseField.getText ().toString ();
        String city = mCityField.getText ().toString ();
        String country = mCountryField.getText ().toString ();
        String designer = mDesignerField.getText ().toString ();
        String owner = mOwnerField.getText ().toString ();
        String gemstone = mGemstoneField.getText ().toString ();
        String cut = mCutField.getText ().toString ();
        String period = mPeriodField.getText ().toString ();
        String obs = mObsField.getText ().toString ();
        String dateFrom = mDateFromField.getText ().toString ();
        dateFrom = dateFrom.equals ("DD-MM-YYYY")? "" :dateFrom;
        String dateTo = mDateToField.getText ().toString ();
        dateTo = dateTo.equals ("DD-MM-YYYY")? "" :dateTo;

        Intent intent = new Intent(getActivity(), JewelsByAuctionActivity.class);
        Bundle searchData = new Bundle ();

        if(! jewelType.equals (""))
            searchData.putString (BUNDLE_DATA_JEWEL_TYPE,jewelType);
        if(! auctionHouse.equals (""))
            searchData.putString (BUNDLE_DATA_AUCTION_HOUSE,auctionHouse);
        if(! city.equals (""))
            searchData.putString (BUNDLE_DATA_CITY,city);
        if(! country.equals (""))
            searchData.putString (BUNDLE_DATA_COUNTRY,country);
        if(! designer.equals (""))
            searchData.putString (BUNDLE_DATA_DESIGNER,designer);
        if(! owner.equals (""))
            searchData.putString (BUNDLE_DATA_OWNER,owner);
        if(! gemstone.equals (""))
            searchData.putString (BUNDLE_DATA_GEMSTONE,gemstone);
        if(! cut.equals (""))
            searchData.putString (BUNDLE_DATA_CUT,cut);
        if(! period.equals (""))
            searchData.putString (BUNDLE_DATA_PERIOD,period);
        if(! obs.equals (""))
            searchData.putString (BUNDLE_DATA_OBS,obs);
        if(! dateFrom.equals ("")) {
            Date date = null;
            try {
                date = formatSP.parse (dateFrom);
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            searchData.putString (BUNDLE_DATA_DATE_FROM, String.format("%1$tY-%1$tm-%1$td",date));
        }
        if(! dateTo.equals ("")) {
            Date date = null;
            try {
                date = formatSP.parse (dateTo);
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            searchData.putString (BUNDLE_DATA_DATE_TO, String.format("%1$tY-%1$tm-%1$td",date));
        }
        intent.putExtra (EXTRA_BUNDLE_SEARCH,searchData);
//        intent.putExtra(EXTRA_FK_AUCTION_ID, fkAuctionId);
        startActivityForResult(intent, AuctionsFragment.REQUEST_OPEN_CATALOGUE);
    }


    private void onClickListener(CursorAdapter adapter, String tableName, String fieldName, int title, TextInputEditText tiet){
        // Creamos un diálogo de búsqueda y lo abrimos
        SearchingDialog searchingDialog = new SearchingDialog (getActivity (), adapter, tableName, fieldName, title,null);

        searchingDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {

            @Override
            public void onDismiss(DialogInterface dialog){
                tiet.setText (searchingDialog.textReturned);
            }
        });
    }
}