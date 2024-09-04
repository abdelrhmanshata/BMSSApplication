package com.example.bmssapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bmssapplication.Model.Goal;
import com.example.bmssapplication.Model.User;
import com.example.bmssapplication.R;
import com.example.bmssapplication.Registration.LoginActivity;
import com.example.bmssapplication.databinding.ActivityProfileBinding;
import com.example.bmssapplication.databinding.DeleteMessageLayoutBinding;
import com.example.bmssapplication.databinding.UpdatePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");
    Toolbar toolbar;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.profile));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUserData();

        binding.btnSave.setOnClickListener(v -> {
            validationInput();
        });

        binding.updatePassword.setOnClickListener(v -> {
            showUpdatePasswordLayout();
        });
    }

    void getUserData() {
        binding.progressCircle.setVisibility(View.VISIBLE);
        refUsers.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    currentUser = user;
                    binding.inputUserName.setText(user.getUserName());
                    binding.inputAge.setText(String.valueOf(user.getuAge()));
                }
                binding.progressCircle.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressCircle.setVisibility(View.GONE);
            }
        });
    }

    void validationInput() {

        binding.progressCircle.setVisibility(View.VISIBLE);

        String inputUserName = Objects.requireNonNull(binding.inputUserName.getText()).toString().trim();
        if (inputUserName.isEmpty()) {
            binding.inputUserName.setError(getString(R.string.nameIsRequired));
            binding.inputUserName.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        String inputAge = binding.inputAge.getText().toString();
        if (TextUtils.isEmpty(inputAge)) {
            binding.inputAge.setError(getString(R.string.age_is_required));
            binding.inputAge.requestFocus();
            binding.progressCircle.setVisibility(View.INVISIBLE);
            return;
        }

        currentUser.setUserName(inputUserName);
        currentUser.setuAge(Integer.parseInt(inputAge));

        updateUser(currentUser);
    }


    public void showUpdatePasswordLayout() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.update_password, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));


        // Initialize ViewBinding for the layout
        UpdatePasswordBinding passwordBinding = UpdatePasswordBinding.bind(dialogView);

        passwordBinding.btnUpdate.setOnClickListener(v -> {

            String currentPassword = Objects.requireNonNull(passwordBinding.currentPassword.getText()).toString().trim();
            if (currentPassword.isEmpty()) {
                passwordBinding.currentPassword.setError(getString(R.string.passwordIsRequired));
                passwordBinding.currentPassword.requestFocus();
                return;
            }

            String newPassword = Objects.requireNonNull(passwordBinding.newPassword.getText()).toString().trim();
            if (newPassword.isEmpty()) {
                passwordBinding.newPassword.setError(getString(R.string.passwordIsRequired));
                passwordBinding.newPassword.requestFocus();
                return;
            }

            if (newPassword.length() < 8) {
                passwordBinding.newPassword.setError(getString(R.string.minimumLength));
                passwordBinding.newPassword.requestFocus();
                return;
            }

            String confirmPassword = Objects.requireNonNull(passwordBinding.confirmPassword.getText()).toString().trim();
            if (!confirmPassword.equals(newPassword)) {
                passwordBinding.confirmPassword.setError(getString(R.string.password_not_match));
                passwordBinding.confirmPassword.setFocusable(true);
                passwordBinding.confirmPassword.requestFocus();
                return;
            }


            if (!currentPassword.equals(currentUser.getuPassword())) {
                passwordBinding.currentPassword.setError(getString(R.string.password_is_wrong));
                passwordBinding.currentPassword.requestFocus();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getuEmail(), currentPassword);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        currentUser.setuPassword(newPassword);
                        updateUser(currentUser);
                        alertDialog.dismiss();
                    } else {
                        try {
                            throw task1.getException();
                        } catch (Exception e) {
                            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                    }
                });
            });
        });

    }


    void updateUser(User user) {
        refUsers.child(user.getuID()).setValue(user).addOnSuccessListener(unused -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            Toasty.success(this, getString(R.string.update_successfully), Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(e -> {
            binding.progressCircle.setVisibility(View.INVISIBLE);
            if (e instanceof FirebaseNetworkException) {
                Toast.makeText(this, getString(R.string.noConnection), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button click event
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}