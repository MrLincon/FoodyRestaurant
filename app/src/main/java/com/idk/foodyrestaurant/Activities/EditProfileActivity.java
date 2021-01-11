package com.idk.foodyrestaurant.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idk.foodyrestaurant.R;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    Button update;
    TextInputLayout layout_email, layout_name, layout_division, layout_country,layout_time, layout_day,layout_location;
    TextInputEditText et_email, et_name, et_division, et_country, et_time, et_day, et_location;
    private Toolbar toolbar;
    private ImageView close;

    private FirebaseAuth mAuth;
    private String userID;

    private FirebaseFirestore db;
    private DocumentReference document_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        toolbar = findViewById(R.id.toolbar);
        close = findViewById(R.id.back);

        layout_name = findViewById(R.id.layout_name);
        layout_division = findViewById(R.id.layout_division);
        layout_country = findViewById(R.id.layout_country);
        layout_email = findViewById(R.id.layout_email);
        layout_time = findViewById(R.id.layout_time);
        layout_day = findViewById(R.id.layout_day);
        layout_location = findViewById(R.id.layout_location);

        et_name = findViewById(R.id.name);
        et_division = findViewById(R.id.et_division);
        et_country = findViewById(R.id.et_country);
        et_email = findViewById(R.id.email_sign_up);
        et_time = findViewById(R.id.et_time);
        et_day = findViewById(R.id.et_day);
        et_location = findViewById(R.id.et_location);

        update = findViewById(R.id.btn_update);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        db = FirebaseFirestore.getInstance();
        document_reference = db.collection("RestaurantDetails").document(userID);

        et_division.setFocusable(false);
        et_country.setFocusable(false);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        et_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Division");
                final String[] division = {"Dhaka", "Chattogram", "Sylhet", "Khulna", "Barisal", "Rajshahi", "Rangpur", "Mymensingh"};
                builder.setSingleChoiceItems(division, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_division.setText(division[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        et_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Country");
                final String[] country = {"Bangladesh"};
                builder.setSingleChoiceItems(country, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_country.setText(country[which]);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = et_name.getText().toString().trim();
                final String email = et_email.getText().toString().trim();
                final String country = et_country.getText().toString().trim();
                final String division = et_division.getText().toString().trim();
                final String time = et_time.getText().toString().trim();
                final String day = et_day.getText().toString().trim();
                final String location = et_location.getText().toString().trim();

                document_reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                document_reference.update("name", name,
                                        "email",email,
                                        "country",country,
                                        "division", division,
                                        "time",time,
                                        "day",day,
                                        "location",location).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                final String id = document_reference.getId();

                                Map<String, Object> userMap = new HashMap<>();

                                userMap.put("name", name);
                                userMap.put("email", email);
                                userMap.put("country", country);
                                userMap.put("division", division);
                                userMap.put("time", time);
                                userMap.put("day", day);
                                userMap.put("id", id);
                                userMap.put("location", location);

                                document_reference.set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(EditProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });
                Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Load data

        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Name = documentSnapshot.getString("name");
                    String Email = documentSnapshot.getString("email");
                    String Division = documentSnapshot.getString("division");
                    String Country = documentSnapshot.getString("country");
                    String Time = documentSnapshot.getString("time");
                    String Day = documentSnapshot.getString("day");
                    String Location = documentSnapshot.getString("location");

                    et_name.setText(Name);
                    et_email.setText(Email);
                    et_division.setText(Division);
                    et_country.setText(Country);
                    et_time.setText(Time);
                    et_day.setText(Day);
                    et_location.setText(Location);

                } else {
                    Toast.makeText(EditProfileActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}


