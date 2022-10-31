package com.example.catalogos.init_package.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.catalogos.auctions_package.auctions.AuctionsFragment;

import com.example.catalogos.general_data.GeneralDataFragment;
import com.example.catalogos.jewels_package.jewel_search.JewelSearchFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    int numOfTabs;


    public PagerAdapter(@NonNull FragmentManager fm, int behavior){
        super (fm, behavior);
        numOfTabs = behavior;
    }


    @NonNull
    @Override
    public Fragment getItem(int position){

        switch (position){
            case 0:
                return AuctionsFragment.newInstance ();
            case 1:
                return JewelSearchFragment.newInstance ();
            case 2:
                return GeneralDataFragment.newInstance ();
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return numOfTabs;
    }
}
