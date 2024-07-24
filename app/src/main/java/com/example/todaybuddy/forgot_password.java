package com.example.todaybuddy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.todaybuddy.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class forgot_password extends AppCompatActivity {
    String email;
    FirebaseAuth auth;
    ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        binding.resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Objects.requireNonNull(binding.email.getEditText()).getText().toString().trim();
                Log.d("EmailDebug", "Email: " + email);
                if (!TextUtils.isEmpty(email) && isValidEmail(email) && isValidDomain(email)) {
                    Resetpassword();
                } else {
                    binding.email.setError("Invalid email format");
                }
            }
        });
    }

    private boolean isValidDomain(String email) {
        String[] parts = email.split("@");
        if (parts.length == 2) {
            String domain = parts[1];
            return domain.endsWith(".com") || domain.endsWith(".org") || domain.endsWith(".net") || domain.endsWith(".ac.in");
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }


    private void Resetpassword() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.resetpassword.setVisibility(View.GONE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.email.getEditText().setText("");
                    startActivity(new Intent(forgot_password.this, Login_Activity.class));
                    finish();
                } else {
                    Toast.makeText(forgot_password.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("password", task.getException().toString());
                    binding.progressBar.setVisibility(View.GONE);
                    binding.resetpassword.setVisibility(View.VISIBLE);
                    binding.email.setError("Failed to send reset email");
                }
            }
        });
    }
}
