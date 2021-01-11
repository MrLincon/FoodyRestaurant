package com.idk.foodyrestaurant.Models;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.idk.foodyrestaurant.Tabs.FragmentFeed;
import com.idk.foodyrestaurant.Tabs.FragmentOffers;
import com.idk.foodyrestaurant.Tabs.FragmentRestaurants;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        final Fragment[] fragment = {null};

        if (position == 0) {
            fragment[0] = new FragmentFeed();
        } else if (position == 1) {
            fragment[0] = new FragmentOffers();
        } else if (position == 2) {
            fragment[0] = new FragmentRestaurants();
        }
        return fragment[0];
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        if (position == 0) {
            return "Feed";
        }  else if (position == 1) {
            return "Offers";
        }else if (position == 2) {
            return "Restaurants";
        }
        return null;
    }

}
