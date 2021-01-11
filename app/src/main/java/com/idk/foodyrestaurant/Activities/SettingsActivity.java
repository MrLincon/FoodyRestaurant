package com.idk.foodyrestaurant.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idk.foodyrestaurant.Authentication.LoginActivity;
import com.idk.foodyrestaurant.Models.ThemeSettings;
import com.idk.foodyrestaurant.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView back;

    private TextView name,division;

    private CardView profile, logOut, darkMode;

    ThemeSettings themeSettings;
    private Switch darkModeSwitch;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseFirestore db;
    private DocumentReference document_reference;

    private String userID;

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
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        logOut = findViewById(R.id.log_out);
        darkModeSwitch = findViewById(R.id.switch_dark_mode);
        darkMode = findViewById(R.id.dark_mode);

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.tv_name);
        division = findViewById(R.id.tv_division);

//        For Action Bar
        toolbarTitle.setText("Settings");
//...............

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();
        document_reference = db.collection("RestaurantDetails").document(userID);

        darkModeSwitch.setClickable(false);
        if (themeSettings.loadNightModeState() == false) {
            darkModeSwitch.setChecked(false);
        } else {
            darkModeSwitch.setChecked(true);
        }

        darkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeSettings.loadNightModeState() == false) {
                    themeSettings.setNightModeState(true);
                    restartApp();   //Recreate activity
                } else {
                    themeSettings.setNightModeState(false);
                    restartApp();   //Recreate activity
                }
            }
        });

        loadUserData();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this,ProfileActivity.class);
                startActivity(i);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signOut = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(signOut);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent close = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(close);
                finish();
            }
        });

    }

    private void loadUserData() {

        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Name = documentSnapshot.getString("name");
                    String Division = documentSnapshot.getString("division");

                    name.setText(Name);
                    division.setText(Division);

                } else {
                    Toast.makeText(SettingsActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


    private void restartApp() {
        Intent restartApp = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(restartApp);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}