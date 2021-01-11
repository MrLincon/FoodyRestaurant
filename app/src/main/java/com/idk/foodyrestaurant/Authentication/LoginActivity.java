package com.idk.foodyrestaurant.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.idk.foodyrestaurant.Activities.MainActivity;
import com.idk.foodyrestaurant.R;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText email_field,password_field;
    TextInputLayout email_layout,password_layout;
    Button login;

    TextView forgot_password;

    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        email_layout = findViewById(R.id.email_layout);
        password_layout = findViewById(R.id.password_layout);

        email_field = findViewById(R.id.userEmail);
        password_field = findViewById(R.id.userPassword);
        login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.login_progressbar);

        forgot_password = findViewById(R.id.forgot_password);

        progressBar.setVisibility(View.GONE);
        login.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_field.getText().toString().trim();
                String password = password_field.getText().toString().trim();

                if (email.isEmpty()) {
                    email_layout.setError("E-mail is required");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    email_layout.setError("Please enter a valid email");
                    return;
                }

                if (password.isEmpty()) {
                    password_layout.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    password_layout.setError("Minimum length of password should be 6");
                    return;
                }
                else{
                    login.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                updateUI();
                            }else {
                                login.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgot_password = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
                startActivity(forgot_password);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });

    }

    private void updateUI() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user!=null){
            updateUI();
        }
    }
}
