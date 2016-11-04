package org.dewaal.dan.dwhomecontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Dan on 23/09/2016.
 */

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public MainPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new Tab_Home_Fragment();
            case 1:
                return new Tab_TVRoom_Fragment();
            case 2:
                return new Tab_Entrance_Fragment();
            case 3:
                return new Tab_Garage_Fragment();
            case 4:
                return new Tab_Deck_Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
