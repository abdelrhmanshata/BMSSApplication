package com.example.bmssapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bmssapplication.Adapter.AdapterGoals;
import com.example.bmssapplication.Adapter.AdapterQuestion;
import com.example.bmssapplication.Model.Question;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.ActivityCommonQuestionBinding;

import java.util.ArrayList;
import java.util.List;

public class CommonQuestionActivity extends AppCompatActivity implements AdapterQuestion.OnItemClickListener {

    ActivityCommonQuestionBinding binding;
    Toolbar toolbar;

    List<Question> questionList;
    AdapterQuestion adapterQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommonQuestionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.common_question);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }


    void init() {
        questionList = new ArrayList<>();
        questionList.add(new Question("1", "What is a budget management system (BMS)?", "A budget management system (BMS) is a software application designed to help individuals and organizations effectively plan, track, and manage their finances. It allows users to set budgets, track expenses, analyze spending patterns, and make informed financial decisions."));
        questionList.add(new Question("2", "What are the key features of this budget management system?", "Our BMS application offers features such as:\n" + "Expense tracking and categorization\n" + "Budget planning and goal setting\n" + "Real-time synchronization across devices\n" + "Data visualization through charts and graphs\n" + "Secure cloud storage for financial data\n" + "Reminders and notifications for upcoming bills or payments\n"));
        questionList.add(new Question("3", "How can I get started with the BMS application?", "To get started, download the app from the Google Play Store (for Android) or the App Store (for iOS). Once installed, create an account or sign in if you already have one. Follow the on-screen prompts to set up your budget categories, add expenses, and start tracking your finances.\n"));
        questionList.add(new Question("4", "Is my financial data secure within the BMS application?", "Yes, we take security seriously. Your financial data is encrypted and stored securely on our servers. We also implement authentication mechanisms to ensure only authorized users have access to their accounts.\n"));
        questionList.add(new Question("5", "Can I access the BMS application from multiple devices?", "Absolutely! The BMS application supports multi-device access. Simply log in to your account from any device, and your data will sync across all devices in real-time, ensuring you always have the latest financial information at your fingertips.\n"));
        questionList.add(new Question("6", "What kind of insights can I gain from using the BMS application?", "By using our BMS application, you can gain insights into your spending habits, identify areas where you can save money, track progress towards your financial goals, and make data-driven decisions to improve your overall financial health."));
        questionList.add(new Question("7", "Does the BMS application support budget adjustments and customization?", "Yes, you can easily adjust your budgets, create custom categories, set spending limits, and tailor the app to fit your unique financial needs and goals.\n"));
        questionList.add(new Question("8", "How often is the BMS application updated?", "We strive to provide regular updates to enhance app functionality, improve user experience, and address any security or performance issues. Make sure to enable automatic updates on your device to get the latest features and improvements."));

        adapterQuestion = new AdapterQuestion(this, questionList, this);
        binding.commonQuestionRecyclerView.setAdapter(adapterQuestion);
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

    @Override
    public void onItem_Click(int position) {

    }
}