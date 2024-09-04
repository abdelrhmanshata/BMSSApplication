package com.example.bmssapplication.Fragment;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Funnel;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.bmssapplication.Notification.NotificationHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.example.bmssapplication.Model.Planning;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.FragmentPlanningBinding;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class PlanningFragment extends Fragment {

    FragmentPlanningBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refPlanning = database.getReference("Planning");

    Planning planning;
    String GraphType = "CircleChart";
    Pie pie;
    List<DataEntry> data;
    ArrayList<PieEntry> entries;

    public PlanningFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPlanningBinding.inflate(inflater, container, false);

        init();
        getPlanning();
        onCLickListener();

        return binding.getRoot();
    }

    void init() {
        planning = new Planning();
        data = new ArrayList<>();
        entries = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void getPlanning() {
        String createdDate = getPlanDate(String.valueOf(LocalDate.now()));
        refPlanning.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFound = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Planning objPlanning = snapshot.getValue(Planning.class);
                    if (objPlanning != null) {
                        String date = getPlanDate(objPlanning.getCreatedDate());
                        if (createdDate.equals(date)) {
                            planning = objPlanning;
                            isFound = true;
                        }
                    }
                }

                if (isFound) {
                    binding.graphPlanLayout.setVisibility(View.VISIBLE);
                    binding.currentDatePlan.setText(getPlanDate(planning.getCreatedDate()));
                    showGraph();
                } else {
                    binding.currentDatePlan.setText(R.string.no_plan_for_current_month);
                    binding.graphPlanLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void onCLickListener() {
        binding.radioGroupPlanType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.circleChartPlan) {
                GraphType = "CircleChart";
            } else if (checkedId == R.id.consumptionPlan) {
                GraphType = "ConsumptionPlan";
            }
            showGraph();
        });

        binding.btnSave.setOnClickListener(v -> {
            if (!binding.homeInput.getText().toString().isEmpty()) {
                planning.setConHome(Float.parseFloat(binding.homeInput.getText().toString()) + planning.getConHome());
                if (Float.parseFloat(binding.homeInput.getText().toString()) > (planning.getHome() / 30)) {
                    NotificationHelper.showNotification(getContext(), 1, getString(R.string.warning_home), getString(R.string.home_exceeded_daily_consumption));
                }
            }

            if (!binding.foodInput.getText().toString().isEmpty()) {
                planning.setConFood(Float.parseFloat(binding.foodInput.getText().toString()) + planning.getConFood());
                if (Float.parseFloat(binding.foodInput.getText().toString()) > (planning.getFood() / 30)) {
                    NotificationHelper.showNotification(getContext(), 2, getString(R.string.warning_food), getString(R.string.food_exceeded_daily_consumption));
                }
            }

            if (!binding.transportationInput.getText().toString().isEmpty()) {
                planning.setConTransportation(Float.parseFloat(binding.transportationInput.getText().toString()) + planning.getConTransportation());
                if (Float.parseFloat(binding.transportationInput.getText().toString()) > (planning.getTransportation() / 30)) {
                    NotificationHelper.showNotification(getContext(), 3, getString(R.string.warning_transportation), getString(R.string.transportation_exceeded_daily_consumption));
                }
            }

            if (!binding.educationInput.getText().toString().isEmpty()) {
                planning.setConEducation(Float.parseFloat(binding.educationInput.getText().toString()) + planning.getConEducation());
                if (Float.parseFloat(binding.educationInput.getText().toString()) > (planning.getEducation() / 30)) {
                    NotificationHelper.showNotification(getContext(), 4, getString(R.string.warning_education), getString(R.string.education_exceeded_daily_consumption));
                }
            }

            if (!binding.entertainmentInput.getText().toString().isEmpty()) {
                planning.setConEntertainment(Float.parseFloat(binding.entertainmentInput.getText().toString()) + planning.getConEntertainment());
                if (Float.parseFloat(binding.entertainmentInput.getText().toString()) > (planning.getEntertainment() / 30)) {
                    NotificationHelper.showNotification(getContext(), 5, getString(R.string.warning_entertainment), getString(R.string.entertainment_exceeded_daily_consumption));
                }
            }

            if (!binding.healthInput.getText().toString().isEmpty()) {
                planning.setConHealth(Float.parseFloat(binding.healthInput.getText().toString()) + planning.getConHealth());
                if (Float.parseFloat(binding.healthInput.getText().toString()) > (planning.getHealth() / 30)) {
                    NotificationHelper.showNotification(getContext(), 6, getString(R.string.warning_health), getString(R.string.health_exceeded_daily_consumption));
                }
            }

            if (!binding.invoicesInput.getText().toString().isEmpty()) {
                planning.setConInvoices(Float.parseFloat(binding.invoicesInput.getText().toString()) + planning.getConInvoices());
                if (Float.parseFloat(binding.invoicesInput.getText().toString()) > (planning.getInvoices() / 30)) {
                    NotificationHelper.showNotification(getContext(), 7, getString(R.string.warning_invoices), getString(R.string.invoices_exceeded_daily_consumption));
                }
            }

            if (!binding.othersInput.getText().toString().isEmpty()) {
                planning.setConOthers(Float.parseFloat(binding.othersInput.getText().toString()) + planning.getConOthers());
                if (Float.parseFloat(binding.othersInput.getText().toString()) > (planning.getOthers() / 30)) {
                    NotificationHelper.showNotification(getContext(), 8, getString(R.string.warning_others), getString(R.string.others_exceeded_daily_consumption));
                }
            }

            binding.homeInput.setText("");
            binding.foodInput.setText("");
            binding.transportationInput.setText("");
            binding.educationInput.setText("");
            binding.entertainmentInput.setText("");
            binding.healthInput.setText("");
            binding.invoicesInput.setText("");
            binding.othersInput.setText("");

            savePlan(planning);
        });
    }

    void showGraph() {
        if (GraphType.equals("CircleChart")) {
            binding.pieChartGraphPlan.setVisibility(View.VISIBLE);
            binding.consumptionPlanLayout.setVisibility(View.GONE);
            CircleChart();
        } else if (GraphType.equals("ConsumptionPlan")) {
            binding.pieChartGraphPlan.setVisibility(View.GONE);
            binding.consumptionPlanLayout.setVisibility(View.VISIBLE);
            consumptionPlan();
        }
    }

    void setCircleDataGraph() {
        entries.clear();
        if (planning.isCheckHome())
            entries.add(new PieEntry(planning.getHome(), getString(R.string.home)));
        if (planning.isCheckFood())
            entries.add(new PieEntry(planning.getFood(), getString(R.string.food)));
        if (planning.isCheckTransportation())
            entries.add(new PieEntry(planning.getTransportation(), getString(R.string.transportation)));
        if (planning.isCheckEducation())
            entries.add(new PieEntry(planning.getEducation(), getString(R.string.education)));
        if (planning.isCheckEntertainment())
            entries.add(new PieEntry(planning.getEntertainment(), getString(R.string.entertainment)));
        if (planning.isCheckHealth())
            entries.add(new PieEntry(planning.getHealth(), getString(R.string.health)));
        if (planning.isCheckInvoices())
            entries.add(new PieEntry(planning.getInvoices(), getString(R.string.invoices)));
        if (planning.isCheckOthers())
            entries.add(new PieEntry(planning.getOthers(), getString(R.string.others)));
        entries.add(new PieEntry(planning.getSaving(), getString(R.string.saving)));
    }

    void CircleChart() {
        setCircleDataGraph();
        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.plan));
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        binding.pieChartGraphPlan.setData(data);

        binding.pieChartGraphPlan.setEntryLabelTextSize(12f);
        binding.pieChartGraphPlan.setCenterTextSize(20f);

        binding.pieChartGraphPlan.setEntryLabelColor(Color.BLACK);
        binding.pieChartGraphPlan.setCenterText(getString(R.string.planning));
        binding.pieChartGraphPlan.getDescription().setEnabled(false);
        binding.pieChartGraphPlan.getDescription().setTextSize(16f);
        binding.pieChartGraphPlan.invalidate(); // refresh chart
    }

    void consumptionPlan() {
        binding.homeLayout.setVisibility(planning.isCheckHome() ? View.VISIBLE : View.GONE);
        binding.home.setText(planning.getConHome() + " / " + planning.getHome());

        binding.foodLayout.setVisibility(planning.isCheckFood() ? View.VISIBLE : View.GONE);
        binding.food.setText(planning.getConFood() + " / " + planning.getFood());

        binding.transportationLayout.setVisibility(planning.isCheckTransportation() ? View.VISIBLE : View.GONE);
        binding.transportation.setText(planning.getConTransportation() + " / " + planning.getTransportation());

        binding.educationLayout.setVisibility(planning.isCheckEducation() ? View.VISIBLE : View.GONE);
        binding.education.setText(planning.getConEducation() + " / " + planning.getEducation());

        binding.entertainmentLayout.setVisibility(planning.isCheckEntertainment() ? View.VISIBLE : View.GONE);
        binding.entertainment.setText(planning.getConEntertainment() + " / " + planning.getEntertainment());

        binding.healthLayout.setVisibility(planning.isCheckHealth() ? View.VISIBLE : View.GONE);
        binding.health.setText(planning.getConHealth() + " / " + planning.getHealth());

        binding.invoicesLayout.setVisibility(planning.isCheckInvoices() ? View.VISIBLE : View.GONE);
        binding.invoices.setText(planning.getConInvoices() + " / " + planning.getInvoices());

        binding.othersLayout.setVisibility(planning.isCheckOthers() ? View.VISIBLE : View.GONE);
        binding.others.setText(planning.getConOthers() + " / " + planning.getOthers());
    }

    void savePlan(Planning currentPlanning) {
        refPlanning.child(currentPlanning.getUserID()).child(currentPlanning.getID()).setValue(currentPlanning).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toasty.success(getActivity(), getString(R.string.save_plan_successfully), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toasty.error(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}