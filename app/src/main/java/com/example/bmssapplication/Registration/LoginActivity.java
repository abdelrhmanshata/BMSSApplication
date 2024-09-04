package com.example.bmssapplication.Registration;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bmssapplication.Activity.HomeActivity;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.ActivityLoginBinding;
import com.example.bmssapplication.databinding.ForgetPasswordLayoutBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListener();
    }

    void onClickListener() {
        binding.registerAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        binding.btnLogin.setOnClickListener(v -> {
            validationInput();
        });

        binding.forgetPassword.setOnClickListener(v -> {
            showDialogForgetPasswordLayout();
        });
    }

    void validationInput() {
        binding.progressCircle.setVisibility(View.VISIBLE);

        String inputEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        if (inputEmail.isEmpty()) {
            binding.inputEmail.setError(getString(R.string.emailIsRequired));
            binding.inputEmail.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            binding.inputEmail.setError(getString(R.string.please_enter_valid_email));
            binding.inputEmail.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputPassword = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (inputPassword.isEmpty()) {
            binding.inputPassword.setError(getString(R.string.passwordIsRequired));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }
        if (inputPassword.length() < 8) {
            binding.inputPassword.setError(getString(R.string.minimumLength));
            binding.inputPassword.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        login(inputEmail, inputPassword);
    }

    void login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                binding.progressCircle.setVisibility(View.INVISIBLE);
                Toast.makeText(this, getString(R.string.successLogin), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                ActivityCompat.finishAffinity(this);

            } else if (task.getException() instanceof FirebaseNetworkException) {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                Toast.makeText(this, getString(R.string.userNotFound), Toast.LENGTH_SHORT).show();
            } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {
                Toast.makeText(this, getString(R.string.passwordIncorrect), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error+->" + task.getException(), Toast.LENGTH_SHORT).show();
            }
            binding.progressCircle.setVisibility(View.INVISIBLE);
        });
    }


    public void showDialogForgetPasswordLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forget_password_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        ForgetPasswordLayoutBinding forgetPasswordBinding = ForgetPasswordLayoutBinding.bind(dialogView);

        forgetPasswordBinding.buttonCancel.setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        forgetPasswordBinding.buttonRecover.setOnClickListener(v -> {
            forgetPasswordBinding.progressCircle.setVisibility(View.VISIBLE);
            String inputEmail = Objects.requireNonNull(forgetPasswordBinding.inputRecoverEmail.getText()).toString().trim();
            if (inputEmail.isEmpty()) {
                forgetPasswordBinding.inputRecoverEmail.setError(getString(R.string.emailIsRequired));
                forgetPasswordBinding.inputRecoverEmail.requestFocus();
                forgetPasswordBinding.progressCircle.setVisibility(View.INVISIBLE);
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                forgetPasswordBinding.inputRecoverEmail.setError(getString(R.string.please_enter_valid_email));
                forgetPasswordBinding.inputRecoverEmail.requestFocus();
                forgetPasswordBinding.progressCircle.setVisibility(View.INVISIBLE);
                return;
            }

            firebaseAuth.sendPasswordResetEmail(inputEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toasty.info(LoginActivity.this, getString(R.string.check_your_email) + inputEmail, Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.error(LoginActivity.this, getString(R.string.falid) + Objects.requireNonNull(task.getException()), Toast.LENGTH_SHORT).show();
                }
                forgetPasswordBinding.progressCircle.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toasty.error(LoginActivity.this, "Error ->" + e.getMessage(), Toast.LENGTH_SHORT).show();
                forgetPasswordBinding.progressCircle.setVisibility(View.INVISIBLE);
                alertDialog.dismiss();
            });
        });
    }
}