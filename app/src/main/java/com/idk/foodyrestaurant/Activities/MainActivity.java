package com.idk.foodyrestaurant.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.idk.foodyrestaurant.BottomNav.FragmentHome;
import com.idk.foodyrestaurant.BottomNav.FragmentNotification;
import com.idk.foodyrestaurant.Models.ThemeSettings;
import com.idk.foodyrestaurant.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    ThemeSettings themeSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Theme Settings
        themeSettings = new ThemeSettings(this);
        if (themeSettings.loadNightModeState() == false) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        //...............

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FragmentHome()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new FragmentHome();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    case R.id.nav_notification:
                        selectedFragment = new FragmentNotification();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                        break;
                    case R.id.nav_add:

                        Intent i = new Intent(MainActivity.this,PostActivity.class);
                        startActivity(i);
                        finish();
//                        selectedFragment = new FragmentPost();
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                                selectedFragment).commit();
                        break;
                    case R.id.nav_profile:
                        Intent profile = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(profile);
                        break;
                }
                return true;
            }
        });


        //Hiding bottom nav

//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)
//                bottomNavigationView .getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationViewBehavior());

    }
}