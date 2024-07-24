package com.example.todaybuddy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todaybuddy.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class Login_Activity extends AppCompatActivity {
    private final boolean isPasswordVisible = false;

    private FirebaseAuth auth;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        auth = FirebaseAuth.getInstance();

        textutil.setupPasswordToggle(Login_Activity.this, binding.passwordR, binding.passwordEditText);

        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        binding.forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resetemail();
            }
        });

        binding.registernow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this, register_activity.class));
                finish();
            }
        });
    }

    private void Resetemail() {
        startActivity(new Intent(Login_Activity.this, forgot_password.class));
    }

    private void signInUser() {

        String email = Objects.requireNonNull(binding.emailR.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(binding.passwordR.getEditText()).getText().toString().trim();

        if (validateInputs(email, password)) {
            binding.progressBarlogin.setVisibility(View.VISIBLE);
            binding.signin.setVisibility(View.GONE);
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        binding.progressBarlogin.setVisibility(View.GONE);
                        binding.signin.setVisibility(View.VISIBLE);
                        FirebaseUser user = auth.getCurrentUser();
                        String username = getIntent().getStringExtra("username");
                        Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                        finish();
                    } else {
                        binding.progressBarlogin.setVisibility(View.GONE);
                        binding.signin.setVisibility(View.VISIBLE);
                        binding.passwordR.setError("Incorrect password or email");
                    }
                }
            });
        }
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !isValidDomain(email)) {
            binding.emailR.setError("Valid email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordR.setError("Password is required");
            return false;
        }

        return true;
    }

    private boolean isValidDomain(String email) {
        String[] parts = email.split("@");
        if (parts.length == 2) {
            String domain = parts[1];
            return domain.endsWith(".com") || domain.endsWith(".org") || domain.endsWith(".net") || domain.endsWith(".ac.in");
        }
        return false;
    }

}
