package com.example.bmssapplication.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.bmssapplication.Model.Planning;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.FragmentHistoryBinding;
import com.example.bmssapplication.databinding.FragmentPlanningBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HistoryFragment extends Fragment {
    FragmentHistoryBinding binding;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refPlanning = database.getReference("Planning");

    List<Planning> planningList;
    List<String> planningNameList;
    ArrayAdapter<String> adapterPlanning;

    Planning planning;
    String GraphType = "CircleChart";
    Pie pie;
    List<DataEntry> data;
    ArrayList<PieEntry> entries;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);

        init();
        getPlanning();
        onCLickListener();

        return binding.getRoot();
    }

    void init() {
        planning = new Planning();
        planningList = new ArrayList<>();
        planningNameList = new ArrayList<>();
        adapterPlanning = new ArrayAdapter<>(getContext(), R.layout.custom_dropdown_item, planningNameList);
        binding.spinner.setAdapter(adapterPlanning);
        data = new ArrayList<>();
        entries = new ArrayList<>();
        pie = AnyChart.pie();
        binding.anyChartView.setProgressBar(binding.progressBar);
        binding.anyChartView.setChart(pie);
    }

    void getPlanning() {
        refPlanning.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                planningList.clear();
                planningNameList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Planning planning = snapshot.getValue(Planning.class);
                    if (planning != null) {
                        planningList.add(planning);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = null;
                        try {
                            date = dateFormat.parse(planning.getCreatedDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
                        planningNameList.add(monthFormat.format(date) + " - " + yearFormat.format(date));
                    }
                }

                if (planningList.isEmpty()) {
                    binding.textNotFound.setVisibility(View.VISIBLE);
                    binding.graphLayout.setVisibility(View.GONE);
                } else {
                    binding.textNotFound.setVisibility(View.GONE);
                    binding.graphLayout.setVisibility(View.VISIBLE);
                    planning = planningList.get(0);
                }
                adapterPlanning.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    void onCLickListener() {
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                // Handle item selection
                planning = planningList.get(position);
                showGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
        binding.radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.circleChart) {
                GraphType = "CircleChart";
            } else if (checkedId == R.id.pieChart) {
                GraphType = "PieChart";
            }
            showGraph();
        });
    }


    void showGraph() {
        if (GraphType.equals("CircleChart")) {
            binding.pieChartGraph.setVisibility(View.VISIBLE);
            binding.anyChartView.setVisibility(View.GONE);
            CircleChart();
        } else if (GraphType.equals("PieChart")) {
            binding.pieChartGraph.setVisibility(View.GONE);
            binding.anyChartView.setVisibility(View.VISIBLE);
            PieChart();
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
        binding.pieChartGraph.setData(data);

        binding.pieChartGraph.setEntryLabelTextSize(12f);
        binding.pieChartGraph.setCenterTextSize(20f);
        binding.pieChartGraph.setEntryLabelColor(Color.BLACK);
        binding.pieChartGraph.setCenterText(getString(R.string.planning));
        binding.pieChartGraph.getDescription().setEnabled(false);
        binding.pieChartGraph.invalidate(); // refresh chart
    }

    void setPlanningDataGraph() {
        data.clear();
        data.add(new ValueDataEntry(getString(R.string.home), planning.getHome()));
        data.add(new ValueDataEntry(getString(R.string.food), planning.getFood()));
        data.add(new ValueDataEntry(getString(R.string.transportation), planning.getTransportation()));
        data.add(new ValueDataEntry(getString(R.string.education), planning.getEducation()));
        data.add(new ValueDataEntry(getString(R.string.entertainment), planning.getEntertainment()));
        data.add(new ValueDataEntry(getString(R.string.health), planning.getHealth()));
        data.add(new ValueDataEntry(getString(R.string.invoices), planning.getInvoices()));
        data.add(new ValueDataEntry(getString(R.string.others), planning.getOthers()));
        data.add(new ValueDataEntry(getString(R.string.saving), planning.getSaving()));
    }

    void PieChart() {
        setPlanningDataGraph();
        pie.data(data);
        pie.labels().position("outside");
        pie.autoRedraw(true);
        pie.legend().title().enabled(true);
        pie.legend().title().text("Planning Details").padding(0d, 0d, 10d, 0d);
        pie.legend().position("center-bottom").itemsLayout(LegendLayout.HORIZONTAL).align(Align.CENTER);
        binding.pieChartGraph.invalidate();
    }
}