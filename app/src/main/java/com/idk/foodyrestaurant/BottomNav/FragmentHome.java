package com.idk.foodyrestaurant.BottomNav;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.idk.foodyrestaurant.Activities.ProfileActivity;
import com.idk.foodyrestaurant.Activities.SettingsActivity;
import com.idk.foodyrestaurant.Models.ViewPagerAdapter;
import com.idk.foodyrestaurant.R;

public class FragmentHome extends Fragment {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ImageView settings;
//    LinearLayout filter;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);


        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        settings = view.findViewById(R.id.settings);

        tabLayout = view.findViewById(R.id.tabLayoutID);
        viewPager = view.findViewById(R.id.viewPager);


        //        For Action Bar
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText("Foody (R)");


        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0, true);


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(getContext(), SettingsActivity.class);
                startActivity(settings);
            }
        });

        return view;
    }
}
