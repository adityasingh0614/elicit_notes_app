package com.example.todaybuddy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.todaybuddy.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class register_activity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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
        //Password toggle
        textutil.setupPasswordToggle(register_activity.this, binding.passwordR, binding.passwordedittext);
        textutil.setupPasswordToggle(register_activity.this, binding.Repeatpassword, binding.Repeatpasswordedittext);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        binding.loginR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(register_activity.this, Login_Activity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String name = Objects.requireNonNull(binding.Name.getEditText()).getText().toString().trim();
        String email = Objects.requireNonNull(binding.emailR.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(binding.passwordR.getEditText()).getText().toString().trim();
        String repeatPassword = Objects.requireNonNull(binding.Repeatpassword.getEditText()).getText().toString().trim();

        if (validateInputs(name, email, password, repeatPassword)) {
            binding.progressBarregister.setVisibility(View.VISIBLE);
            binding.register.setVisibility(View.GONE);
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        binding.progressBarregister.setVisibility(View.GONE);
                        binding.register.setVisibility(View.VISIBLE);
                        FirebaseUser user = auth.getCurrentUser();
                        Intent intent = new Intent(register_activity.this, Login_Activity.class);
                        intent.putExtra("username", name);
                        Log.d("register_activity", "Registration successful, starting Login_Activity with username: " + name);
                        startActivity(intent);
                        Toast.makeText(register_activity.this, "Registration successful", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.e("register_activity", "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(register_activity.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateInputs(String name, String email, String password, String repeatPassword) {
        TextInputLayout nameInputLayout = binding.Name;
        TextInputLayout emailInputLayout = binding.emailR;
        TextInputLayout passwordInputLayout = binding.passwordR;
        TextInputLayout repeatPasswordInputLayout = binding.Repeatpassword;

        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError("Name is required");
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !isValidDomain(email)) {
            emailInputLayout.setError("Valid email is required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required");
            return false;
        }

        if (password.length() < 8) {
            passwordInputLayout.setError("Password must be at least 8 characters");
            return false;
        }

        if (!isValidPassword(password)) {
            passwordInputLayout.setError("Password must include an uppercase letter, a lowercase letter, a digit, and a special character");
            return false;
        }

        if (!password.equals(repeatPassword)) {
            repeatPasswordInputLayout.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isLowerCase(c)) hasLowercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecialChar = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
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
