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

import com.example.bmssapplication.Adapter.AdapterGoals;
import com.example.bmssapplication.Model.Goal;
import com.example.bmssapplication.Model.User;
import com.example.bmssapplication.Notification.NotificationHelper;
import com.example.bmssapplication.R;
import com.example.bmssapplication.databinding.AddGoalBinding;
import com.example.bmssapplication.databinding.FragmentGoalsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class GoalsFragment extends Fragment implements AdapterGoals.OnItemClickListener {
    FragmentGoalsBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refGoals = database.getReference("Goals");
    DatabaseReference refUsers = database.getReference("Users");

    List<Goal> goalList;
    AdapterGoals adapterGoals;

    User currentUser;

    public GoalsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGoalsBinding.inflate(inflater, container, false);
        init();
        getUser();
        getGoals();
        onClickListener();
        return binding.getRoot();
    }

    void init() {
        goalList = new ArrayList<>();
        adapterGoals = new AdapterGoals(getContext(), goalList, this);
        binding.goalsRecyclerView.setAdapter(adapterGoals);
    }

    void onClickListener() {
        binding.addNewGoal.setOnClickListener(v -> {
            showDialogAddGoalLayout(null);
        });
    }

    void getGoals() {
        binding.progressBar.setVisibility(View.VISIBLE);
        refGoals.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goalList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        goalList.add(goal);
                    }
                }
                binding.progressBar.setVisibility(View.GONE);
                adapterGoals.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItem_Click(int position) {
        Goal goal = goalList.get(position);
        showDialogAddGoalLayout(goal);
    }

    public void showDialogAddGoalLayout(Goal currentGoal) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_goal, null);
        dialogBuilder.setView(dialogView);
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        // Initialize ViewBinding for the layout
        AddGoalBinding addGoalBinding = AddGoalBinding.bind(dialogView);
        addGoalBinding.title.setText(currentGoal == null ? getString(R.string.add_new_goal) : getString(R.string.update_goal));

        Goal goal;
        if (currentGoal == null) {
            goal = new Goal();
            goal.setUserID(firebaseUser.getUid());
            goal.setID(refGoals.push().getKey());
        } else {
            goal = currentGoal;
            addGoalBinding.inputGoalName.setText(goal.getGoalName());
            addGoalBinding.inputGoalTarget.setText(String.valueOf(goal.getTarget()));
            addGoalBinding.inputGoalSaving.setText(String.valueOf(goal.getSaving()));
        }

        addGoalBinding.btnSave.setOnClickListener(v -> {

            String inputGoalName = Objects.requireNonNull(addGoalBinding.inputGoalName.getText()).toString().trim();
            if (inputGoalName.isEmpty()) {
                addGoalBinding.inputGoalName.setError(getString(R.string.goal_name_is_required));
                addGoalBinding.inputGoalName.requestFocus();
                return;
            }
            String inputGoalTarget = Objects.requireNonNull(addGoalBinding.inputGoalTarget.getText()).toString().trim();
            if (inputGoalTarget.isEmpty()) {
                addGoalBinding.inputGoalTarget.setError(getString(R.string.goal_target_is_required));
                addGoalBinding.inputGoalTarget.requestFocus();
                return;
            }

            String inputGoalSaving = Objects.requireNonNull(addGoalBinding.inputGoalSaving.getText()).toString().trim();
            inputGoalSaving = inputGoalSaving.isEmpty() ? "0.0" : inputGoalSaving;


            if ((Float.parseFloat(inputGoalSaving) - goal.getSaving()) > currentUser.getSaving()) {
                Toasty.warning(getContext(), R.string.you_do_not_have_this_amount, Toast.LENGTH_SHORT).show();
            } else {

                currentUser.setSaving(currentUser.getSaving() - (Float.parseFloat(inputGoalSaving) - goal.getSaving()));

                goal.setGoalName(inputGoalName);
                goal.setTarget(Float.parseFloat(inputGoalTarget));
                goal.setSaving(Float.parseFloat(inputGoalSaving));

                if (goal.getSaving() >= goal.getTarget()) {
                    NotificationHelper.showNotification(getContext(), (int) System.currentTimeMillis(), getContext().getString(R.string.goals), getContext().getString(R.string.congratulations_you_have_achieved_your_goal) + " " + goal.getGoalName());
                }

                refGoals.child(firebaseUser.getUid()).child(goal.getID()).setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasty.success(getActivity(), getString(R.string.save_goal_successfully), Toast.LENGTH_SHORT).show();
                            updateUser(currentUser);
                        }
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
                        Log.d("Error Goals Fragment", e.getMessage().toString());
                    }
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