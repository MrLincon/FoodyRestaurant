package com.idk.foodyrestaurant.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.idk.foodyrestaurant.R;

public class PasswordRecoveryActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressBar progressBar;
    EditText email;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        toolbar = findViewById(R.id.toolbar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Password Recovery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.pw_progressbar);
        send = findViewById(R.id.send);
        email = findViewById(R.id.recoveryEmail);

        progressBar.setVisibility(View.GONE);
        send.setVisibility(View.VISIBLE);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String recoveryEmail = email.getText().toString().trim();

                if (recoveryEmail.isEmpty()) {
                    email.setError("Enter a valid email");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    send.setVisibility(View.GONE);

                    FirebaseAuth.getInstance().sendPasswordResetEmail(recoveryEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PasswordRecoveryActivity.this, "A password reset link has been sent to your email address", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            send.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }
}
