package com.example.bmssapplication.Fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.bmssapplication.Adapter.AdapterDebits;
import com.example.bmssapplication.Model.Debit;
import com.example.bmssapplication.Model.User;
import com.example.bmssapplication.Notification.NotificationHelper;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.AddDebitBinding;
import com.example.bmssapplication.databinding.FragmentDebitsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class DebitsFragment extends Fragment implements AdapterDebits.OnItemClickListener {

    FragmentDebitsBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refDebits = database.getReference("Debits");
    DatabaseReference refUsers = database.getReference("Users");

    List<Debit> debitList;
    AdapterDebits adapterDebits;
    User currentUser;

    public DebitsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDebitsBinding.inflate(inflater, container, false);

        init();
        getUser();
        getDebits();
        onClickListener();

        return binding.getRoot();
    }

    void init() {
        debitList = new ArrayList<>();
        adapterDebits = new AdapterDebits(getContext(), debitList, this);
        binding.debitsRecyclerView.setAdapter(adapterDebits);
    }

    void onClickListener() {
        binding.addNewDebits.setOnClickListener(v -> {
            showDialogAddDebitLayout(null);
        });
    }

    void getDebits() {
        binding.progressBar.setVisibility(View.VISIBLE);
        refDebits.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                debitList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Debit debit = snapshot.getValue(Debit.class);
                    if (debit != null) {
                        debitList.add(debit);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
                adapterDebits.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItem_Click(int position) {
        Debit debit = debitList.get(position);
        showDialogAddDebitLayout(debit);
    }

    public void showDialogAddDebitLayout(Debit currentDebit) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_debit, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        AddDebitBinding addDebitBinding = AddDebitBinding.bind(dialogView);
        addDebitBinding.title.setText(currentDebit == null ? getString(R.string.add_new_debit) : getString(R.string.update_debit));

        Debit debit;
        if (currentDebit == null) {
            debit = new Debit();
            debit.setUserID(firebaseUser.getUid());
            debit.setID(refDebits.push().getKey());
        } else {
            debit = currentDebit;
            addDebitBinding.inputCreditorName.setText(debit.getCreditorName());
            addDebitBinding.inputPurpose.setText(debit.getPurpose());
            addDebitBinding.inputPaid.setText(String.valueOf(debit.getPaid()));
            addDebitBinding.inputTotal.setText(String.valueOf(debit.getTotal()));
        }

        addDebitBinding.btnSave.setOnClickListener(v -> {

            String inputCreditorName = Objects.requireNonNull(addDebitBinding.inputCreditorName.getText()).toString().trim();
            if (inputCreditorName.isEmpty()) {
                addDebitBinding.inputCreditorName.setError(getString(R.string.creditor_name_is_required));
                addDebitBinding.inputCreditorName.requestFocus();
                return;
            }
            String inputPurpose = Objects.requireNonNull(addDebitBinding.inputPurpose.getText()).toString().trim();
            if (inputPurpose.isEmpty()) {
                addDebitBinding.inputPurpose.setError(getString(R.string.purpose_is_required));
                addDebitBinding.inputPurpose.requestFocus();
                return;
            }

            String inputTotal = Objects.requireNonNull(addDebitBinding.inputTotal.getText()).toString().trim();
            if (inputTotal.isEmpty()) {
                addDebitBinding.inputTotal.setError(getString(R.string.total_is_required));
                addDebitBinding.inputTotal.requestFocus();
                return;
            }

            String inputPaid = Objects.requireNonNull(addDebitBinding.inputPaid.getText()).toString().trim();
            inputPaid = inputPaid.isEmpty() ? "0.0" : inputPaid;

            if ((Float.parseFloat(inputPaid) - debit.getPaid()) > currentUser.getSaving()) {
                Toasty.warning(getContext(), R.string.you_do_not_have_this_amount, Toast.LENGTH_SHORT).show();
            } else {
                currentUser.setSaving(currentUser.getSaving() - (Float.parseFloat(inputPaid) - debit.getPaid()));

                debit.setCreditorName(inputCreditorName);
                debit.setPurpose(inputPurpose);
                debit.setPaid(Float.parseFloat(inputPaid));
                debit.setTotal(Float.parseFloat(inputTotal));

                if (debit.getPaid() >= debit.getTotal()) {
                    NotificationHelper.showNotification(getContext(), (int) System.currentTimeMillis(), getContext().getString(R.string.debits), getContext().getString(R.string.congratulations_you_have_paid_off_your_debt) + " " + debit.getPurpose());
                }

                refDebits.child(firebaseUser.getUid()).child(debit.getID()).setValue(debit).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toasty.success(getActivity(), getString(R.string.save_debit_successfully), Toast.LENGTH_SHORT).show();
                        updateUser(currentUser);
                    }
                });
                alertDialog.dismiss();
            }
        });
    }

    void getUser() {
        refUsers.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    currentUser = user;
                    try {
                        binding.userSaving.setText(requireContext().getString(R.string.you_are_saving) + " " + currentUser.getSaving() + " SAR");
                    } catch (Exception e) {
                        Log.d("Error Debits Fragment", e.getMessage().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateUser(User user) {
        refUsers.child(user.getuID()).setValue(user);
    }

}