package com.example.bmssapplication.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.bmssapplication.Activity.CreditCardActivity;
import com.example.bmssapplication.Model.Planning;
import com.example.bmssapplication.Model.User;
import com.example.bmssapplication.Notification.NotificationHelper;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.FragmentHomeBinding;
import com.example.bmssapplication.databinding.SelectPlanningBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refUsers = database.getReference("Users");
    DatabaseReference refPlanning = database.getReference("Planning");

    User currentUser;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        getUser();
        onClick();
        return binding.getRoot();
    }

    void onClick() {
        binding.btnCreate.setOnClickListener(v -> {
            showDialogSelectPlanningLayout();
        });

        binding.btnSetUp.setOnClickListener(v -> {
            showDialogSetupPlanningLayout();
        });
    }

    public void showDialogSelectPlanningLayout() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_planning, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        SelectPlanningBinding planningBinding = SelectPlanningBinding.bind(dialogView);
        Planning planning = new Planning();

        planningBinding.isAdditionalIncome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.layoutAdditionalIncome.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            planning.setAdditionalIncome(isChecked);
        });

        planningBinding.homeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.homeInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckHome(isChecked);
        });
        planningBinding.foodCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.foodInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckFood(isChecked);
        });
        planningBinding.transportationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.transportationInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckTransportation(isChecked);
        });
        planningBinding.educationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.educationInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckEducation(isChecked);
        });
        planningBinding.entertainmentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.entertainmentInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckEntertainment(isChecked);
        });
        planningBinding.healthCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.healthInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckHealth(isChecked);
        });
        planningBinding.invoicesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.invoicesInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckInvoices(isChecked);
        });
        planningBinding.othersCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.othersInput.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
            planning.setCheckOthers(isChecked);
        });

        planningBinding.btnSave.setOnClickListener(v -> {

            String inputSalary = planningBinding.inputSalary.getText().toString();
            if (TextUtils.isEmpty(inputSalary)) {
                planningBinding.inputSalary.setError(getString(R.string.salary_is_required));
                planningBinding.inputSalary.requestFocus();
                return;
            }
            planning.setuSalary(Float.parseFloat(inputSalary));

            String inputSalaryIncome = "0.0";
            if (planningBinding.isAdditionalIncome.isChecked()) {
                inputSalaryIncome = planningBinding.inputSalaryIncome.getText().toString();
                if (TextUtils.isEmpty(inputSalaryIncome)) {
                    planningBinding.inputSalaryIncome.setError(getString(R.string.additional_income_is_required));
                    planningBinding.inputSalaryIncome.requestFocus();
                    return;
                }
            }
            planning.setuSalaryIncome(Float.parseFloat(inputSalaryIncome));

            String homeInput = planningBinding.homeInput.getText().toString();
            float home = homeInput.isEmpty() ? 0.0f : Float.parseFloat(homeInput);
            planning.setHome(home);

            String foodInput = planningBinding.foodInput.getText().toString();
            float food = foodInput.isEmpty() ? 0.0f : Float.parseFloat(foodInput);
            planning.setFood(food);

            String transportationInput = planningBinding.transportationInput.getText().toString();
            float transportation = transportationInput.isEmpty() ? 0.0f : Float.parseFloat(transportationInput);
            planning.setTransportation(transportation);

            String educationInput = planningBinding.educationInput.getText().toString();
            float education = educationInput.isEmpty() ? 0.0f : Float.parseFloat(educationInput);
            planning.setEducation(education);

            String entertainmentInput = planningBinding.entertainmentInput.getText().toString();
            float entertainment = entertainmentInput.isEmpty() ? 0.0f : Float.parseFloat(entertainmentInput);
            planning.setEntertainment(entertainment);

            String healthInput = planningBinding.healthInput.getText().toString();
            float health = healthInput.isEmpty() ? 0.0f : Float.parseFloat(healthInput);
            planning.setHealth(health);

            String invoicesInput = planningBinding.invoicesInput.getText().toString();
            float invoices = invoicesInput.isEmpty() ? 0.0f : Float.parseFloat(invoicesInput);
            planning.setInvoices(invoices);

            String othersInput = planningBinding.othersInput.getText().toString();
            float others = othersInput.isEmpty() ? 0.0f : Float.parseFloat(othersInput);
            planning.setOthers(others);

            if ((planning.getuSalary() + planning.getuSalaryIncome()) >= planning.getSum()) {
                planning.setID(refPlanning.push().getKey());
                planning.setUserID(currentUser.getuID());
                planning.setSaving((planning.getuSalary() + planning.getuSalaryIncome()) - planning.getSum());
//                saveSelectPlan(planning);
                savePlan(planning, "SelectPlan");
                alertDialog.dismiss();
            } else {
                Toasty.warning(getActivity(), getString(R.string.sorry_you_exceeded_salary), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDialogSetupPlanningLayout() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.select_planning, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        SelectPlanningBinding planningBinding = SelectPlanningBinding.bind(dialogView);
        Planning setupPlanning = new Planning();

        planningBinding.isAdditionalIncome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            planningBinding.layoutAdditionalIncome.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            setupPlanning.setAdditionalIncome(isChecked);
        });

        planningBinding.homeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckHome(isChecked);
        });
        planningBinding.foodCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckFood(isChecked);
        });
        planningBinding.transportationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckTransportation(isChecked);
        });
        planningBinding.educationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckEducation(isChecked);
        });
        planningBinding.entertainmentCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckEntertainment(isChecked);
        });
        planningBinding.healthCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckHealth(isChecked);
        });
        planningBinding.invoicesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckInvoices(isChecked);
        });
        planningBinding.othersCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setupPlanning.setCheckOthers(isChecked);
        });

        planningBinding.btnSave.setOnClickListener(v -> {

            String inputSalary = planningBinding.inputSalary.getText().toString();
            if (TextUtils.isEmpty(inputSalary)) {
                planningBinding.inputSalary.setError(getString(R.string.salary_is_required));
                planningBinding.inputSalary.requestFocus();
                return;
            }
            setupPlanning.setuSalary(Float.parseFloat(inputSalary));

            String inputSalaryIncome = "0.0";
            if (planningBinding.isAdditionalIncome.isChecked()) {
                inputSalaryIncome = planningBinding.inputSalaryIncome.getText().toString();
                if (TextUtils.isEmpty(inputSalaryIncome)) {
                    planningBinding.inputSalaryIncome.setError(getString(R.string.additional_income_is_required));
                    planningBinding.inputSalaryIncome.requestFocus();
                    return;
                }
            }
            setupPlanning.setuSalaryIncome(Float.parseFloat(inputSalaryIncome));

            if (currentUser.getNumOfPlans() > 0) {
                setupPlan(setupPlanning);
                setupPlanning.setID(refPlanning.push().getKey());
                setupPlanning.setUserID(currentUser.getuID());
                setupPlanning.setSaving((setupPlanning.getuSalary() + setupPlanning.getuSalaryIncome()) - setupPlanning.getSum());
                savePlan(setupPlanning, "SetupPlan");
            } else {
                Toasty.warning(getContext(), getString(R.string.sorry_you_haven_t_free_plans), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), CreditCardActivity.class));
            }
            alertDialog.dismiss();
        });
    }

    void setupPlan(Planning setupPlanning) {
        float totalSalary = setupPlanning.getuSalary() + setupPlanning.getuSalaryIncome();
        if (totalSalary <= 10000) {
            if (setupPlanning.isCheckHome()) {
                setupPlanning.setHome((float) (totalSalary * 0.15));
            }
            if (setupPlanning.isCheckFood()) {
                setupPlanning.setFood((float) (totalSalary * 0.15));
            }
            if (setupPlanning.isCheckTransportation()) {
                setupPlanning.setTransportation((float) (totalSalary * 0.08));
            }
            if (setupPlanning.isCheckInvoices()) {
                setupPlanning.setInvoices((float) (totalSalary * 0.15));
            }
            if (setupPlanning.isCheckEducation()) {
                setupPlanning.setEducation((float) (totalSalary * 0.05));
            }
            if (setupPlanning.isCheckEntertainment()) {
                setupPlanning.setEntertainment((float) (totalSalary * 0.10));
            }
            if (setupPlanning.isCheckHealth()) {
                setupPlanning.setHealth((float) (totalSalary * 0.05));
            }
            if (setupPlanning.isCheckOthers()) {
                setupPlanning.setOthers((float) (totalSalary * 0.10));
            }
        } else if ((totalSalary > 10000) && (totalSalary <= 30000)) {
            if (setupPlanning.isCheckHome()) {
                setupPlanning.setHome((float) (totalSalary * 0.15));
            }
            if (setupPlanning.isCheckFood()) {
                setupPlanning.setFood((float) (totalSalary * 0.15));
            }
            if (setupPlanning.isCheckTransportation()) {
                setupPlanning.setTransportation((float) (totalSalary * 0.08));
            }
            if (setupPlanning.isCheckInvoices()) {
                setupPlanning.setInvoices((float) (totalSalary * 0.15));
            }
            if (setupPlanning.isCheckEducation()) {
                setupPlanning.setEducation((float) (totalSalary * 0.10));
            }
            if (setupPlanning.isCheckEntertainment()) {
                setupPlanning.setEntertainment((float) (totalSalary * 0.10));
            }
            if (setupPlanning.isCheckHealth()) {
                setupPlanning.setHealth((float) (totalSalary * 0.05));
            }
            if (setupPlanning.isCheckOthers()) {
                setupPlanning.setOthers((float) (totalSalary * 0.10));
            }
        } else if (totalSalary > 30000) {
            if (setupPlanning.isCheckHome()) {
                setupPlanning.setHome((float) (totalSalary * 0.11));
            }
            if (setupPlanning.isCheckFood()) {
                setupPlanning.setFood((float) (totalSalary * 0.11));
            }
            if (setupPlanning.isCheckTransportation()) {
                setupPlanning.setTransportation((float) (totalSalary * 0.08));
            }
            if (setupPlanning.isCheckInvoices()) {
                setupPlanning.setInvoices((float) (totalSalary * 0.10));
            }
            if (setupPlanning.isCheckEducation()) {
                setupPlanning.setEducation((float) (totalSalary * 0.10));
            }
            if (setupPlanning.isCheckEntertainment()) {
                setupPlanning.setEntertainment((float) (totalSalary * 0.06));
            }
            if (setupPlanning.isCheckHealth()) {
                setupPlanning.setHealth((float) (totalSalary * 0.05));
            }
            if (setupPlanning.isCheckOthers()) {
                setupPlanning.setOthers((float) (totalSalary * 0.10));
            }
        }
    }

    void savePlan(Planning currentPlanning, String planType) {
        String currentDate = getPlanDate(currentPlanning.getCreatedDate());
        refPlanning.child(currentPlanning.getUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isNotDateFound = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Planning planning = snapshot.getValue(Planning.class);
                    if (planning != null) {
                        String date = getPlanDate(planning.getCreatedDate());
                        if (currentDate.equals(date)) {
                            isNotDateFound = false;
                            break;
                        }
                    }
                }

                if (isNotDateFound) {

                    if (planType.equals("SetupPlan")) {
                        currentUser.setNumOfPlans(currentUser.getNumOfPlans() - 1);
                        updateUser(currentUser);
                    }

                    refPlanning.child(currentPlanning.getUserID()).child(currentPlanning.getID()).setValue(currentPlanning).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            NotificationHelper.showNotification(getContext(), 0, getString(R.string.saving), getString(R.string.you_will_save) + currentPlanning.getSaving() + " SAR");
                            Toasty.success(getActivity(), getString(R.string.plan_created_successfully), Toast.LENGTH_SHORT).show();
                            currentUser.setSaving(currentUser.getSaving() + currentPlanning.getSaving());
                            updateUser(currentUser);
                        }
                    }).addOnFailureListener(e -> {
                        Toasty.error(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Toasty.warning(getContext(), getString(R.string.there_is_another_plan_in_current_month), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("SimpleDateFormat")
    String getPlanDate(String planningDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(planningDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
        return monthFormat.format(date) + " - " + yearFormat.format(date);
    }

    void getUser() {
        refUsers.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    currentUser = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateUser(User user) {
        refUsers.child(user.getuID()).setValue(user);
    }

}