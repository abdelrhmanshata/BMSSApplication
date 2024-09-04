package com.example.bmssapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.example.bmssapplication.Model.Payment;
import com.example.bmssapplication.Model.User;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.ActivityCreditCardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class CreditCardActivity extends AppCompatActivity {

    ActivityCreditCardBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refPayment = database.getReference("Payment");
    DatabaseReference refUsers = database.getReference("Users");

    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {

            int numOfPlan, totalPrice;
            String text = "Pay";

            if (s.toString().isEmpty()) {
                text = "Pay";
            } else {
                numOfPlan = Integer.parseInt(s.toString());
                totalPrice = numOfPlan * 5;
                text = "Pay " + totalPrice + " SAR";
            }
            binding.buttonSave.setText(text);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardForm
                .cardholderName(CardForm.FIELD_REQUIRED)
                .cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(this);
        binding.cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.inputNumberOfPlans.addTextChangedListener(searchTextWatcher);

        binding.buttonSave.setOnClickListener(v -> {
            checkout();
        });


    }

    void checkout() {
        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        loading(true);

        String inputCardholderName = binding.cardForm.getCardholderName();
        if (inputCardholderName.isEmpty()) {
            Toasty.error(this, getString(R.string.card_holder_name_is_required), Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }

        String inputCardNumber = binding.cardForm.getCardNumber();
        if (inputCardNumber.isEmpty()) {
            Toasty.error(this, getString(R.string.card_number_is_required), Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }

        String inputExpirationMonth = binding.cardForm.getExpirationMonth();
        if (inputExpirationMonth.isEmpty()) {
            Toasty.error(this, getString(R.string.expiration_month_is_required), Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }

        String inputExpirationYear = binding.cardForm.getExpirationYear();
        if (inputExpirationYear.isEmpty()) {
            Toasty.error(this, getString(R.string.expiration_year_is_required), Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }

        String inputCvv = binding.cardForm.getCvv();
        if (inputCvv.isEmpty()) {
            Toasty.error(this, getString(R.string.cvv_is_required), Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }

        String inputNumberOfPlans = Objects.requireNonNull(binding.inputNumberOfPlans.getText()).toString();
        if (inputNumberOfPlans.isEmpty()) {
            binding.inputNumberOfPlans.setError(getString(R.string.number_of_plan_must_be_required));
            binding.inputNumberOfPlans.setFocusable(true);
            binding.inputNumberOfPlans.requestFocus();
            loading(false);
            return;
        }

        // Set Data IN Firebase
        Payment payment = new Payment();
        payment.setuID(firebaseUser.getUid());
        payment.setPaymentID(refPayment.push().getKey());
        payment.setCardholderName(inputCardholderName);
        payment.setCardNumber(inputCardNumber);
        payment.setExpirationMonth(inputExpirationMonth);
        payment.setExpirationYear(inputExpirationYear);
        payment.setCvv(inputCvv);
        payment.setNumOfPlan(Integer.parseInt(inputNumberOfPlans));
        float totalPrice = Integer.parseInt(inputNumberOfPlans) * 5;
        payment.setTotalPrice(totalPrice);

        // Save Data
        saveToDatabase(payment);
    }

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSave.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSave.setVisibility(View.VISIBLE);
        }
    }

    void saveToDatabase(Payment payment) {
        refPayment.child(firebaseUser.getUid()).child(payment.getPaymentID()).setValue(payment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                refUsers.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            int numOfPlans = user.getNumOfPlans() + payment.getNumOfPlan();
                            user.setNumOfPlans(numOfPlans);
                            refUsers.child(firebaseUser.getUid()).setValue(user);
                            Toasty.success(CreditCardActivity.this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreditCardActivity.this, HomeActivity.class));
                            finishAffinity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toasty.error(CreditCardActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
        loading(false);
    }

}