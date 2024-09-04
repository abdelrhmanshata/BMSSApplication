package com.example.bmssapplication.Fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bmssapplication.Activity.CommonQuestionActivity;
import com.example.bmssapplication.Activity.ProfileActivity;
import com.example.bmssapplication.Model.Debit;
import com.example.bmssapplication.Model.Goal;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.AddGoalBinding;
import com.example.bmssapplication.databinding.ContactusLayoutBinding;
import com.example.bmssapplication.databinding.DeleteMessageLayoutBinding;
import com.example.bmssapplication.databinding.FragmentPlanningBinding;
import com.example.bmssapplication.databinding.FragmentSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SettingsFragment extends Fragment {


    FragmentSettingsBinding binding;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        binding.profile.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProfileActivity.class));
        });

        binding.contactUs.setOnClickListener(v -> {
            showDialogContactUsLayout();
        });

        binding.commonQuestion.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CommonQuestionActivity.class));
        });
        return binding.getRoot();
    }

    public void showDialogContactUsLayout() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.contactus_layout, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        ContactusLayoutBinding contactusLayoutBinding = ContactusLayoutBinding.bind(dialogView);
        contactusLayoutBinding.mailText.setOnClickListener(v -> {

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setType("text/plain");
            emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{contactusLayoutBinding.mailText.getText().toString()});
            startActivity(emailIntent);
        });
        contactusLayoutBinding.twitterText.setOnClickListener(v -> {
            openLink(contactusLayoutBinding.twitterText.getText().toString());
        });
        contactusLayoutBinding.instagramText.setOnClickListener(v -> {
            openLink(contactusLayoutBinding.instagramText.getText().toString());
        });
    }

    public void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}