package com.example.bmssapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.bmssapplication.R;
import com.example.bmssapplication.Registration.LoginActivity;
import com.example.bmssapplication.databinding.ActivityIntroBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;
    private final static int SPLASH_DISPLAY_LENGTH = 1000; //change time
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.lottieAnimationView.playAnimation();

        new Handler().postDelayed(() -> {
            if (firebaseUser != null) {
                startActivity(new Intent(IntroActivity.this, HomeActivity.class));
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
            ActivityCompat.finishAffinity(this);
        }, SPLASH_DISPLAY_LENGTH);
    }
}